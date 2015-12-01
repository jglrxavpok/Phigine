import org.jglr.phiengine.client.input.{Controller, ControllerHandler, Controllers, Input}
import org.jglr.phiengine.client.render.g2d.{Skeleton, Sprite, SpriteBatch}
import org.jglr.phiengine.client.render.{Colors, LevelRenderer, Texture}
import org.jglr.phiengine.client.text.{Font, FontRenderer}
import org.jglr.phiengine.client.ui.UI
import org.jglr.phiengine.client.ui.layouts.FlowLayout
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.level.{Level, Physics2DLevel}
import org.jglr.phiengine.core.utils.PhiConfig
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW

class TestGame2(engine: PhiEngine) extends Game(engine: PhiEngine) {
  var batch: SpriteBatch = null
  var logo: Texture = null
  var x: Int = 0
  var y: Int = 0

  override def getName: String = "Test Game2"

  override def update(delta: Float): Unit = {
    ui.update(delta)
    if(testButton.isPressed) {
      println(":D")
    }
  }

  var sprite: Sprite = null

  var level: Level = null
  var lvlRenderer: LevelRenderer = null
  var font: FontRenderer = null
  var ui: UI = null
  var controller: Controller = null
  var controller2: Controller = null
  var controllerHandler: ControllerHandler = null
  var testButton: Input = null
  var testSkeleton: Skeleton = null

  override def init(config: PhiConfig): Unit = {
    batch = new SpriteBatch
    logo = new Texture("logo.png")
    addInputListener(new TestInput(this))
    setIcon(new FilePointer("icon64.png"))
    sprite = new Sprite(logo)

    level = new Physics2DLevel(new Vector2f())
    lvlRenderer = new LevelRenderer(level)
    val ent = new Entity(level)
    ent.addComponent(classOf[TestComponent])
    level.spawnEntity(ent)

    font = new FontRenderer(FontRenderer.ASCII, Font.get("Consolas", 28, antialias = false))
    ui = new UI(font)
    val layout = new FlowLayout(ui, 5f, 5f, FlowLayout.CENTER)
    ui.layout = layout
    val testPane = new TestPanel(font)
    ui.addChild(testPane)

    controllerHandler = new ControllerHandler(engine)
    for(i <- 0 to GLFW.GLFW_JOYSTICK_LAST) {
      println(i+": "+GLFW.glfwJoystickPresent(i))
    }
    val id = Controllers.findFirstID()
    println("found: "+id)
    controller = new Controller(id)
    controllerHandler.registerController(controller)
    controller.setListener(testPane)

    testButton = controllerHandler.addButton(controller, 2, "Test")

    testSkeleton = new TestSkeleton()
  }

  override def pollEvents(): Unit = {
    controllerHandler.poll()
  }

  override def render(delta: Float): Unit = {
    setBackgroundColor(Colors.cyan)
    batch.begin()
  //  batch.draw(logo, x - logo.getWidth / 2, y - logo.getHeight / 2)
    batch.draw(logo, 0, 0)
    sprite.draw(batch)
    testSkeleton.position.set(x,y)
    testSkeleton.render(delta, batch)
    batch.end()
    lvlRenderer.render(delta)
    ui.render(delta)
    font.renderString(engine.timer.getUPS + " UPS", 0, engine.getDisplayHeight-font.font.getHeight('A')*2, 0,Colors.niceWhite)
    font.renderString(engine.timer.getFPS + " FPS", 0, engine.getDisplayHeight-font.font.getHeight('A'), 0,Colors.niceWhite)
  }
}
