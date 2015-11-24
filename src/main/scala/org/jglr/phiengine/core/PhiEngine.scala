package org.jglr.phiengine.core

import java.awt.image.{DataBufferInt, BufferedImage}
import java.nio.charset.Charset
import java.util
import javax.imageio.ImageIO

import com.codedisaster.steamworks.SteamAPI
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson._
import org.jglr.phiengine.client.WindowPointer
import org.jglr.phiengine.client.input._
import org.jglr.phiengine.client.render.deferred.{GBufferAttachs, GBuffer}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.render._
import org.jglr.phiengine.client.render.g3d.{Model, DMXModelLoader}
import org.jglr.phiengine.client.render.lighting.{AmbientLight, LightComponent, PointLight}
import org.jglr.phiengine.client.text.{FontFormat, Font, FontRenderer}
import org.jglr.phiengine.client.utils.{LWJGLSetup, Timer}
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.io.{FileType, Assets, FilePointer}
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.maths.YepppNativesSetup
import org.jglr.phiengine.core.utils._
import org.jglr.phiengine.network.channels.PhiChannel
import org.jglr.phiengine.network.{Server, NetworkSide, NetworkHandler}
import org.joml.{Vector4f, Vector3f, Matrix4f}
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWvidmode
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.{File, IOException}
import java.lang.reflect.Constructor
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.jglr.phiengine.core.utils.JavaConversions._

import org.lwjgl.opengl.GL30._

import scala.collection.JavaConversions._

object PhiEngine {
  private var instance: PhiEngine = null
  private val version = Version.create("=phiversion=")

  def getVersion: Version = {
    version
  }

  def crash(message: String) {
    crash(message, null)
  }

  def crash(message: String, reason: Throwable) {
    val builder: StringBuilder = new StringBuilder
    val indent: String = "  "
    builder.append("[==== PhiEngine Crash Report ====]\n")
    builder.append(indent).append("Error message: ").append(message).append("\n")
    if (reason != null) {
      builder.append(indent).append("Reason: ").append(reason.getClass.getCanonicalName).append(": ").append(reason.getMessage).append("\n")
      var i: Int = 0
      for (elem <- reason.getStackTrace) {
        if (i == 0) builder.append(indent).append("Stack trace: at ").append(elem.toString).append("\n")
        else builder.append(indent).append("             at ").append(elem.toString).append("\n")
        i += 1
      }

      if(reason.getCause != null) {
        appendCause(builder, reason.getCause)
      }
    }
    builder.append(indent).append("[==== Engine Infos ====]\n")
    builder.append(indent).append(indent).append(s"Engine version: $version\n")
    builder.append(indent).append("[==== Game Infos ====]\n")
    if (PhiEngine.getInstance != null && PhiEngine.getInstance.game != null) builder.append(indent).append(indent).append("Game Name: ").append(PhiEngine.getInstance.game.getName).append("\n")
    else builder.append(indent).append(indent).append("Game Name: UNKNOWN\n")
    System.err.println(builder.toString())
    forceExit(-1)
  }

  private def appendCause(builder: StringBuilder, cause: Throwable): Unit = {
    val indent: String = "  "
    builder.append(indent).append("Caused by: ").append(cause.getClass.getCanonicalName).append(": ").append(cause.getMessage).append("\n")
    var i: Int = 0
    for (elem <- cause.getStackTrace) {
      if (i == 0) builder.append(indent).append("Stack trace: at ").append(elem.toString).append("\n")
      else builder.append(indent).append("             at ").append(elem.toString).append("\n")
      i += 1
    }

    if(cause.getCause != null)
      appendCause(builder, cause.getCause)
  }

  private def forceExit(errorCode: Int) {
    if (instance != null) {
      instance.dispose()
    }
    System.exit(errorCode)
  }

  def getInstance: PhiEngine = {
    instance
  }

  def start(gameClass: Class[_ <: Game], config: PhiConfig) {
    var engine: PhiEngine = null
    var game: Game = null
    try {
      val cons: Constructor[_ <: Game] = gameClass.getConstructor(classOf[PhiEngine])
      engine = new PhiEngine
      game = cons.newInstance(engine)
      engine.init(game, config)
    }
    catch {
      case e: Exception => {
        crash("Error while loading game", e)
      }
    }
    try {
      if (engine != null && game != null) {
        engine.loop()
      }
    }
    catch {
      case e: Exception => {
        crash("Error while running " + game.getName, e)
      }
    }
  }
}

class PhiEngine extends PhigineBase {

  var timer: Timer = null
  private var game: Game = null
  var displayWidth: Int = 0
  var displayHeight: Int = 0
  private var window: WindowPointer = null
  private var inputHandler: InputHandler = null
  private var inputQueue: InputProcessorQueue = null
  private var stopKey: Input = null
  private var projectionMatrix: Matrix4f = null
  private var backgroundColor = Colors.black
  var autoUpdates: Boolean = false
  private val tickableQueue = new util.LinkedList[ITickable]
  private val tickableRemovalQueue = new util.LinkedList[ITickable]
  var logo: Texture = null
  private var screenshotKey: Input = null
  private var takingScreenshot = false

  // Rendering
  private var mainFramebuffer: GBuffer = null
  private var geometryShader: Shader = null
  private var sphere: Model = null
  private var pointLightShader: Shader = null
  private var defaultShader: Shader = null

  // Variables used to render text on loading
  private var loadingY = 0f
  private var loadingFontRenderer: FontRenderer = null
  private var loadingScroll = 0f
  private val loadingLines = new util.ArrayList[String]
  private var loadingBatch: SpriteBatch = null
  var assets: Assets = null

  // Everything related to Steamworks
  private var usesSteamworks = false

  // Stuff related to natives handling
  var nativesFolder: File = null

  PhiEngine.instance = this

  def autoUpdate(tickable: ITickable) = {
    if(autoUpdates)
    tickableQueue.add(tickable)
  }

  def stopAutoUpdate(tickable: ITickable): Unit = {
    tickableRemovalQueue.add(tickable)
  }

  def shutdown(): Unit = {
    running = false
  }

  override def init(game: Game, config: PhiConfig) {
    super.init(game, config)
    EngineStart.handle(this, config)
    assets = new Assets(this, game)
    setProjectionMatrix(new Matrix4f().setOrtho(0, getDisplayWidth, getDisplayHeight, 0, -100, 100))
    this.game = game
    initLJWGL(config)
    YepppNativesSetup.load(nativesFolder, logger)
    loadingFontRenderer = new FontRenderer(FontRenderer.ASCII, Font.get("Consolas", 18, antialias = false))
    loadingBatch = loadingFontRenderer.batch
    logo = new Texture("logo.png")
    displayLoadingStep("Loaded fonts")
    displayLoadingStep("Loading assets")
    assets.load()
    if(config.loadTexturesAtLaunch) {
      displayLoadingStep("Preloading textures")
      val textures = assets.preload.get("textures")
      for(t <- textures) {
        displayLoadingStep(s"  Preloading $t")
        new Texture(t)
      }
    }
    if(config.loadShadersAtLaunch) {
      displayLoadingStep("Preloading shaders")
      val shaders = assets.preload.get("shaders")
      for(s <- shaders) {
        displayLoadingStep(s"  Preloading $s")
        new Shader(s)
      }
    }
    if(config.usesSteamAPI) {
      displayLoadingStep("Loading Steam API...")
      SteamNativesSetup.load(nativesFolder, logger)
      usesSteamworks = true
    }
    displayLoadingStep("Now loading timer")
    timer = new Timer
    timer.init
    displayLoadingStep("Now loading network code")
    val server: Server = networkHandler.newServer
    displayLoadingStep("Now loading "+game.getName)
    game.init(config)
    running = true
    val keyboardController = new KeyboardController(this)
    stopKey = inputHandler.createKey(GLFW_KEY_ESCAPE, "Stop key")
    screenshotKey = inputHandler.createKey(GLFW_KEY_F2, "Screenshot key")
  }

  def displayLoadingStep(text: String): Unit = {
    if(running)
    return
    loadingBatch.begin()
    loadingBatch.draw(logo, displayWidth-logo.getWidth, 0, 0)
    if(game.getLogo != null) {
      val gLogo = game.getLogo
      loadingBatch.draw(gLogo, displayWidth-gLogo.getWidth, displayHeight-gLogo.getHeight, 0)
    }
    loadingLines.add(text)
    val lineHeight = loadingFontRenderer.font.getHeight('A')
    loadingY += lineHeight
    if(loadingY >= displayHeight) {
      loadingScroll += lineHeight
    }
    for(i <- 0 until loadingLines.size()) {
      val line = loadingLines.get(i)
      loadingFontRenderer.renderString(line, 0, i*lineHeight-loadingScroll, 0, Colors.niceWhite, 1f, loadingBatch)
    }
    loadingBatch.end()
    window.swapBuffers
    window.pollEvents
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }

  override def update(delta: Float) {
    game.update(delta)
    while(!tickableQueue.isEmpty) {
      val tickable = tickableQueue.remove(0)
      tickableRegistry.register(tickable.toString, tickable)
    }
    while(!tickableRemovalQueue.isEmpty) {
      val tickable = tickableRemovalQueue.remove(0)
      tickableRegistry.delete(tickable.toString)
    }
    tickableRegistry.foreachValue((t: ITickable) => if(t.shouldAutoUpdate) t.tick(delta))

    if (usesSteamworks && SteamAPI.isSteamRunning) {
      SteamAPI.runCallbacks()
    }
  }

  override def pollEvents() {
    inputQueue.drain()
    if (stopKey.isPressed) {
      running = false
    }

    if(screenshotKey.isPressed) {
      if(!takingScreenshot) {
        takeScreenshot()
      }
      takingScreenshot = true
    } else {
      takingScreenshot = false
    }
    game.pollEvents()
  }

  private def performGeometryPass(alpha: Float) = {
    geometryShader.bind()
    //mainFramebuffer.bindGeometryPass()
    glDepthMask(true)
    glEnable(GL_DEPTH_TEST)
    glDepthFunc(GL_LEQUAL)

    glClearStencil(0)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT)
    mainFramebuffer.bindWriting()
    glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT)

    game.render(alpha)

    glDepthMask(false)
  }

  def calcPointLightBSphere(p: PointLight): Float = {
    val maxChannel = Math.max(Math.max(p.color.x, p.color.y), p.color.z)
    // the attenuation model is a quadratic equation, therefore in order to find then it reaches 0, we first calculate the discriminant
    val discriminant = -p.attenuationCoefficients.y - 4*p.attenuationCoefficients.x*(p.attenuationCoefficients.z - 256 * maxChannel * p.intensity)
    if(discriminant < 0) {
      // no real roots, abandon ship!
      0f
    } else {
      val root = (-p.attenuationCoefficients.y + Math.sqrt(discriminant)) / (2 * p.attenuationCoefficients.x)
      root.toFloat
    }
  }

  def findAllLights[T](lightClass: Class[T]): util.List[T] = {
    val lvl = game.getLevel
    val list = new util.ArrayList[T]
    if(lvl != null) {
      for(l <- lvl.components(classOf[LightComponent]).filter((p: LightComponent) => lightClass.isAssignableFrom(p.light.getClass))) {
        list.add(l.light.asInstanceOf[T])
      }
    }
    list
  }

  def findAllPointLights(): util.List[PointLight] = {
    findAllLights(classOf[PointLight])
  }

  private def performLightingPass(alpha: Float): Unit = {
    /*for(i <- 0 until 4) {
      val x: Int = ((i % 2) * (displayWidth/2f)).toInt
      val y: Int = (((4-i-1) / 2) * (displayHeight/2f)).toInt
      mainFramebuffer.copyToWindowArea(GL_COLOR_ATTACHMENT0+i, x, y, displayWidth/2, displayHeight/2)
    }*/
    mainFramebuffer.copyToWindow(GL_COLOR_ATTACHMENT0)
  }

  override def render(alpha: Float) {
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    //mainFramebuffer.startFrame()
    performGeometryPass(alpha)

    //glEnable(GL_STENCIL_TEST)
    performLightingPass(alpha)

    checkGLError("post rendering")
  }

  override def postLoop(): Unit = {
    window.swapBuffers
    window.pollEvents
    timer.updateFPS
    timer.updateUPS
    timer.update
    if (window.shouldClose) running = false
  }

  private def initLJWGL(config: PhiConfig) {
    try {
      LWJGLSetup.load(nativesFolder, logger)
      GLFW.glfwSetErrorCallback(Callbacks.errorCallbackPrint)
      if (glfwInit == GL_FALSE) {
        PhiEngine.crash("GLFW could not be init... ;c")
      }
      var monitor: Long = MemoryUtil.NULL
      val primaryMonitor: Long = glfwGetPrimaryMonitor
      if (config.fullscreen) {
        monitor = primaryMonitor
      }
      glfwDefaultWindowHints()
      glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
      glfwWindowHint(GLFW_RESIZABLE, if (config.resizable) GL_TRUE else GL_FALSE)
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
      glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
      glfwWindowHint(GLFW_DECORATED, if (config.decorated && !config.fullscreen) GL_TRUE else GL_FALSE)
      window = new WindowPointer(glfwCreateWindow(displayWidth, displayHeight, config.title, monitor, MemoryUtil.NULL))
      val vidmode: GLFWvidmode = new GLFWvidmode(glfwGetVideoMode(primaryMonitor))
      if (!config.fullscreen && config.centered) {
        window.setPos(vidmode.getWidth / 2 - displayWidth / 2, vidmode.getHeight / 2 - displayHeight / 2)
      }
      glfwMakeContextCurrent(window.getPointer)
      glfwSwapInterval(1)
      GLContext.createFromCurrent
      if(config.fullscreen) {
        vidmode.setWidth(config.width)
        vidmode.setHeight(config.height)
      }
      window.setSize(displayWidth, displayHeight)
      inputHandler = new InputHandler(this)
      inputQueue = new InputProcessorQueue(inputHandler)
      window.show
      setInputProcessor(inputQueue)
      glEnable(GL_BLEND)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      sphere = DMXModelLoader.loadModel("assets/models/Sphere.dmx")
      mainFramebuffer = new GBuffer(displayWidth, displayHeight)
      geometryShader = new Shader("assets/shaders/passes/geometry.glsl")
      pointLightShader = new Shader("assets/shaders/passes/pointLight.glsl")
      defaultShader = new Shader("assets/shaders/passes/default.glsl")
    }
    catch {
      case e: IOException => {
        e.printStackTrace()
      }
    }
  }

  private def setInputProcessor(processor: InputProcessor) {
    Callbacks.releaseAllCallbacks(window.getPointer)
    glfwSetCharCallback(window.getPointer, processor.charCallback)
    glfwSetCursorPosCallback(window.getPointer, processor.cursorPosCallback)
    glfwSetKeyCallback(window.getPointer, processor.keyCallback)
    glfwSetMouseButtonCallback(window.getPointer, processor.mouseButtonCallback)
    glfwSetScrollCallback(window.getPointer, processor.scrollCallback)
  }

  override def dispose(): Unit = {
    window.destroy
    glfwTerminate()
    val outputs = Shaders.outputList || Textures.outputList
    if(outputs) {
      val gson: Gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
      val obj = new JsonObject
      if(Shaders.outputList) {
        fromCache(obj, Shaders.cache, "shaders")
      }
      if(Textures.outputList) {
        fromCache(obj, Textures.cache, "textures")
      }
      val json = gson.toJson(obj)
      println(json)
      val output = Files.asCharSink(new File(".", "generatedLists.json"), Charsets.UTF_8)
      output.write(json)
    }
  }

  private def fromCache[T](obj: JsonObject, cache: util.HashMap[FilePointer, T], assetType: String): Unit = {
    val assetList = new JsonArray
    val v = cache.keySet.stream.sorted().forEach((f: FilePointer) => {
      if(f.getType != FileType.VIRTUAL) {
        val t = f.getType
        assetList.add(new JsonPrimitive(t.toString.toLowerCase+":"+f.getPath))
      }
    })
    obj.add(assetType, assetList)
  }

  def checkGLError(location: String) {
    val glError: Int = glGetError
    if (glError != GL_NO_ERROR) {
      logger.error("OpenGL Error: " + GLContext.translateGLErrorString(glError) + " at " + location)
    }
  }

  def getDisplayHeight: Int = {
    displayHeight
  }

  def getDisplayWidth: Int = {
    displayWidth
  }

  def getWindow: WindowPointer = {
    window
  }

  def addInputListener(proc: InputListener) {
    inputQueue.addReceiver(proc)
  }

  def getInputHandler: InputHandler = inputHandler

  def registerKey(input: Input): Unit = {
    inputHandler.getKeys.add(input)
  }

  def registerMouseButton(input: Input): Unit = {
    inputHandler.getMouseButtons.add(input)
  }

  def registerMouseMoveListener(input: Input): Unit = {
    inputHandler.getMouseMoveListeners.add(input)
  }

  def getProjectionMatrix: Matrix4f = {
    projectionMatrix
  }

  def setProjectionMatrix(projectionMatrix: Matrix4f) {
    this.projectionMatrix = projectionMatrix
  }

  def getTime: Double = {
    glfwGetTime
  }

  def getDelta(): Float = timer.getDelta

  def setIcon(icon: FilePointer) {
    getLogger.error("Icons are not yet supported.")
  }

  def setBackgroundColor(color: Color) = {
    backgroundColor = color
  }

  def takeScreenshot(): Unit = {
    val buffer = BufferUtils.createFloatBuffer(displayWidth*displayHeight*4)
    GL11.glReadPixels(0,0,displayWidth,displayHeight, GL_RGBA, GL_FLOAT, buffer)
    val screenshot = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB)
    val screenshotPixels = screenshot.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData
    for(i <- 0 until displayWidth*displayHeight*4 by 4) {
      val red = buffer.get()*255f
      val green = buffer.get()*255f
      val blue = buffer.get()*255f
      val alpha = buffer.get()*255f
      val color = (alpha.toInt << 24) | (red.toInt << 16) | (green.toInt << 8) | blue.toInt
      val index = i/4
      val x = index % displayWidth
      val y = index / displayWidth
      screenshotPixels(x + (displayHeight-y-1)*displayWidth) = color
    }
    ImageIO.write(screenshot, "png", new File(".", "screenshot_"+timer.getTime+".png"))
  }

  def grabMouse(): Unit = {
    glfwSetInputMode(window.getPointer, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
  }

  def ungrabMouse(): Unit = {
    glfwSetInputMode(window.getPointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
  }

  def getMainFrameBuffer: Framebuffer = mainFramebuffer
}
