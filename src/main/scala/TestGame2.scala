import org.jglr.phiengine.client.render.{Colors, LevelRenderer, Texture}
import org.jglr.phiengine.client.render.g2d.{Sprite, SpriteBatch}
import org.jglr.phiengine.client.text.{FontFormat, FontRenderer, Font}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.level.{Level, PhysicsLevel}
import org.jglr.phiengine.core.maths.Vec2
import org.jglr.phiengine.core.utils.PhiConfig

class TestGame2(engine: PhiEngine) extends Game(engine: PhiEngine) {
  var batch: SpriteBatch = null
  var logo: Texture = null
  var x: Int = 0
  var y: Int = 0

  override def getName: String = "Test Game2"

  override def update(delta: Float): Unit = {}

  var sprite: Sprite = null

  var level: Level = null
  var lvlRenderer: LevelRenderer = null
  var font: FontRenderer = null

  override def init(config: PhiConfig): Unit = {
    batch = new SpriteBatch
    logo = new Texture("logo.png")
    addInputProcessor(new TestInput(this))
    setIcon(new FilePointer("icon64.png"))
    sprite = new Sprite(logo)

    level = new PhysicsLevel(new Vec2())
    lvlRenderer = new LevelRenderer(level)
    val ent = new Entity(level)
    ent.addComponent(classOf[TestComponent])
    level.spawnEntity(ent)

    font = new FontRenderer(FontRenderer.ASCII, Font.get("Roboto", 20, FontFormat.ITALIC, FontFormat.BOLD))
  }

  override def render(delta: Float): Unit = {
    setBackgroundColor(Colors.cyan)
    batch.begin()
    batch.draw(logo, x - logo.getWidth / 2, y - logo.getHeight / 2)
    batch.draw(logo, 0, 0)
    sprite.draw(batch)
    batch.end()
    lvlRenderer.render(delta)
    font.renderString(s"Hi there, I'm a test string created from a TrueType font (${font.font.getName()})! :D", 10, engine.getDisplayHeight()-42, 0, Colors.darkGray)
  }
}
