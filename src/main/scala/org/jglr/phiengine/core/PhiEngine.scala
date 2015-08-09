package org.jglr.phiengine.core

import org.jglr.phiengine.client.WindowPointer
import org.jglr.phiengine.client.input._
import org.jglr.phiengine.client.render.{Colors, Color}
import org.jglr.phiengine.client.utils.{LWJGLSetup, Timer}
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.maths.Mat4
import org.jglr.phiengine.core.utils._
import org.jglr.phiengine.network.channels.PhiChannel
import org.jglr.phiengine.network.{Server, NetworkSide, NetworkHandler}
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWvidmode
import org.lwjgl.opengl.GLContext
import org.lwjgl.system.MemoryUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.reflect.Constructor
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.jglr.phiengine.core.utils.JavaConversions._

object PhiEngine {
  private var instance: PhiEngine = null
  private val version = Version.create("=phiversion=")

  def getVersion(): Version = {
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
    }
    builder.append(indent).append("[==== Engine Infos ====]\n")
    builder.append(indent).append(indent).append(s"Engine version: $version\n")
    builder.append(indent).append("[==== Game Infos ====]\n")
    if (PhiEngine.getInstance != null) builder.append(indent).append(indent).append("Game Name: ").append(PhiEngine.getInstance.game.getName).append("\n")
    else builder.append(indent).append(indent).append("Game Name: UNKNOWN\n")
    System.err.println(builder.toString())
    forceExit(-1)
  }

  private def forceExit(errorCode: Int) {
    if (instance != null) {
      instance.dispose()
    }
    System.exit(errorCode)
  }

  def getInstance(): PhiEngine = {
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

class PhiEngine extends IDisposable {
  def autoUpdate(tickable: ITickable) = {
    if(autoUpdates)
      tickableRegistry.register(tickable.toString(), tickable)
  }

  private var logger: Logger = null

  var timer: Timer = null
  private var tickableRegistry: Registry[String, ITickable] = null
  private var game: Game = null
  private var displayWidth: Int = 0
  private var displayHeight: Int = 0
  private var window: WindowPointer = null
  private var running: Boolean = false
  private var inputHandler: InputHandler = null
  private var inputQueue: InputProcessorQueue = null
  private var stopKey: Input = null
  private var projectionMatrix: Mat4 = null
  private var networkHandler: NetworkHandler = null
  private var backgroundColor = Colors.black
  var autoUpdates: Boolean = false

  PhiEngine.instance = this

  def init(game: Game, config: PhiConfig) {
    autoUpdates = config.autoUpdates
    projectionMatrix = new Mat4().identity
    tickableRegistry = new Registry[String, ITickable]
    logger = LoggerFactory.getLogger(game.getName)
    logger.info("Loading Phingine "+PhiEngine.getVersion)
    this.game = game
    displayWidth = config.width
    displayHeight = config.height
    initLJWGL(config)
    timer = new Timer
    timer.init
    setProjectionMatrix(new Mat4().orthographic(0, getDisplayWidth(), getDisplayHeight(), 0, -100, 100))
    networkHandler = new NetworkHandler(this)
    networkHandler.registerChannel("PhiEngine", new PhiChannel(NetworkSide.CLIENT))
    val server: Server = networkHandler.newServer
    game.init(config)
    running = true
    val keyboardController = new KeyboardController(this)
    stopKey = inputHandler.createKey(GLFW_KEY_ESCAPE, "Stop key")
  }

  def loop() {
    window.show
    var delta: Float = 0
    var accumulator: Float = 0f
    val interval: Float = 1f / 60L
    var alpha: Float = 0
    while (running) {
      delta = timer.getDelta
      accumulator += delta
      pollEvents()
      while (accumulator >= interval) {
        update(accumulator)
        accumulator -= interval
      }
      alpha = accumulator / interval
      render(alpha)
      window.swapBuffers
      window.pollEvents
      timer.updateFPS
      timer.updateUPS
      timer.update
      if (window.shouldClose) running = false
    }
    dispose()
  }

  private def update(delta: Float) {
    game.update(delta)
    tickableRegistry.foreachValue((t: ITickable) => t.tick(delta))
  }

  private def pollEvents() {
    inputQueue.drain()
    if (stopKey.isPressed) {
      running = false
    }
    game.pollEvents()
  }

  private def render(alpha: Float) {
    glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    game.render(alpha)
  }

  private def initLJWGL(config: PhiConfig) {
    try {
      LWJGLSetup.load(SystemUtils.getBaseFolder("Phingine"), logger)
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
      glfwWindowHint(GLFW_DECORATED, if (config.decorated) GL_TRUE else GL_FALSE)
      window = new WindowPointer(glfwCreateWindow(displayWidth, displayHeight, config.title, monitor, MemoryUtil.NULL))
      val vidmode: GLFWvidmode = new GLFWvidmode(glfwGetVideoMode(primaryMonitor))
      if (!config.fullscreen && config.centered) {
        window.setPos(vidmode.getWidth / 2 - config.width / 2, vidmode.getHeight / 2 - config.height / 2)
      }
      glfwMakeContextCurrent(window.getPointer)
      glfwSwapInterval(1)
      GLContext.createFromCurrent
      inputHandler = new InputHandler(this)
      inputQueue = new InputProcessorQueue(inputHandler)
      setInputProcessor(inputQueue)
      glEnable(GL_BLEND)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      glEnable(GL_DEPTH_TEST)
      glDepthFunc(GL_LEQUAL)
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

  def dispose() {
    window.destroy
    glfwTerminate()
  }

  def getLogger(): Logger = {
    logger
  }

  def checkGLError(location: String) {
    val glError: Int = glGetError
    if (glError != GL_NO_ERROR) {
      logger.error("OpenGL Error: " + GLContext.translateGLErrorString(glError) + " at " + location)
    }
  }

  def getTickableRegistry(): Registry[String, ITickable] = {
    tickableRegistry
  }

  def getDisplayHeight(): Int = {
    displayHeight
  }

  def getDisplayWidth(): Int = {
    displayWidth
  }

  def getWindow(): WindowPointer = {
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

  def getProjectionMatrix(): Mat4 = {
    projectionMatrix
  }

  def setProjectionMatrix(projectionMatrix: Mat4) {
    this.projectionMatrix = projectionMatrix
  }

  def getTime(): Double = {
    glfwGetTime
  }

  def setIcon(icon: FilePointer) {
    getLogger().error("Icons are not yet supported.")
  }

  def getNetworkHandler(): NetworkHandler = {
    networkHandler
  }

  def setBackgroundColor(color: Color) = {
    backgroundColor = color
  }
}