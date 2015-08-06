import org.jglr.phiengine.client.input.Input
import org.jglr.phiengine.client.render.{ColorModel, Texture, Colors}
import org.jglr.phiengine.client.render.g2d.{ShapeMode, ShapeBatch, SpriteBatch}
import org.jglr.phiengine.client.text.{Font, FontRenderer}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.level.{CustomLevel, Level}
import org.jglr.phiengine.core.maths.{Vec2, Vec3}
import org.jglr.phiengine.core.utils.{BaseConversions, PhiConfig}
import BaseConversions._
import org.jglr.phiengine.core.utils.JavaConversions._
import org.jglr.phiengine.core.maths.Constants._
import org.lwjgl.opengl.GL11

import scala.collection.JavaConversions._

import org.lwjgl.glfw.GLFW._

object PongGame extends App {
  val config = new PhiConfig
  config.title = "Pong"
  config.autoUpdates = true
  PhiEngine.start(classOf[Pong], config)
}

class Pong(engine: PhiEngine) extends Game(engine) {
  var batch: SpriteBatch = null
  var shapeBatch: ShapeBatch = null
  var player: Entity = null
  var enemy: Entity = null
  var ball: Entity = null
  var boundaries: Entity = null
  var level: Level = null
  var width: Float = 0f
  var height: Float = 0f
  var up: Input = null
  var down: Input = null

  val padW = 20
  val padH = 150
  var font: FontRenderer = null

  override def getName: String = "Pong"

  override def update(delta: Float): Unit = {
    val posComp = player.getComponent(classOf[PositionComponent])
    val speed = 8
    if(up.isPressed) {
      posComp.velocity(0, -speed, 0)
    }

    if(down.isPressed) {
      posComp.velocity(0, speed, 0)
    }

    if(!up.isPressed && !down.isPressed) {
      posComp.velocity(0, 0, 0)
    }

    move(player)
    move(enemy)

    val ballPComp = ball.getComponent(classOf[PositionComponent])
    if(canGoTo(ball, ballPComp.position + (ballPComp.velocity.x, 0, 0) )) {
      ballPComp.position += (ballPComp.velocity.x, 0,0)
    } else {
      ballPComp.velocity.x *= -1.05f
    }

    if(canGoTo(ball, ballPComp.position+(0, ballPComp.velocity.y, 0))) {
      ballPComp.position += (0, ballPComp.velocity.y, 0)
    } else {
      ballPComp.velocity.y *= -1.05f
    }
  }

  def move(entity: Entity): Unit = {
    val posComp = entity.getComponent(classOf[PositionComponent])
    if(canGoTo(entity, posComp.position+posComp.velocity)) {
      posComp.position += posComp.velocity
    }
  }

  def canGoTo(entity: Entity, pos: Vec3): Boolean = { // TODO: Autocheck for colliding in Level
    val box = entity.getComponent(classOf[AABBComponent]).box.copy(pos.x, pos.y)
    for(e <- level.entities) {
      if(e != entity && e.has(classOf[AABBComponent])) {
        val list = e.getComponents(classOf[AABBComponent])
        for (comp <- list) {
          if (box.collides(comp.box)) {
            return false
          }
        }
      }
    }
    true
  }

  override def init(config: PhiConfig): Unit = {
    font = new FontRenderer(FontRenderer.ASCII, Font.get("Consolas", 18, antialias = false))
    this.width = config.width
    this.height = config.height

    batch = new SpriteBatch()
    shapeBatch = new ShapeBatch(ShapeMode.FILLED)
    level = new CustomLevel()

    boundaries = level.createEntity()
    boundaries.addComponent(classOf[AABBComponent]).setSize(width, 20)
    boundaries.addComponent(classOf[AABBComponent]).setSize(width, 20).setPos(0,height-20-100)
    boundaries.addComponent(classOf[AABBComponent]).setSize(20, height)
    boundaries.addComponent(classOf[AABBComponent]).setSize(20, height).setPos(width-20, 0)
    level.spawnEntity(boundaries)

    player = level.createEntity()
    enemy = level.createEntity()
    ball = level.createEntity()

    val w = padW
    val h = padH
    val playerPos = 80
    // TODO: Builtin PositionComponent, same for AABBComponent
    player.addComponent(classOf[AABBComponent]).setSize(w, h)
    player.addComponent(classOf[PositionComponent]).position(playerPos,height/2-50-h/2,0)

    enemy.addComponent(classOf[AABBComponent]).setSize(w, h)
    enemy.addComponent(classOf[PositionComponent]).position(width-playerPos-w,height/2-50-h/2,0)

    val ballW = 20
    val ballH = 20
    ball.addComponent(classOf[AABBComponent]).setSize(ballW, ballH)
    ball.addComponent(classOf[PositionComponent]).position(width/2-ballW/2,height/2-50-ballH/2,0)
    val angle = Math.random()*TAU
    val speed = 2.5f
    val cos = Math.cos(angle).toFloat*speed
    val sin = Math.sin(angle).toFloat*speed
    ball.getComponent(classOf[PositionComponent]).velocity(cos, sin, 0)

    level.spawnEntity(player)
    level.spawnEntity(enemy)
    level.spawnEntity(ball)

    up = engine.getInputHandler.createKey(GLFW_KEY_UP, "Player up") // TODO: Replacement for GLFW_KEY_UP ?
    down = engine.getInputHandler.createKey(GLFW_KEY_DOWN, "Player up") // TODO: Replacement for GLFW_KEY_DOWN ?
    setBackgroundColor(Colors.darkGray)

    println(engine.getProjectionMatrix().transform(new Vec3(width, height, 0)))
    val invproj = ~engine.getProjectionMatrix()
    println(invproj.transform(new Vec3(1, 1, 0)))
  }

  override def render(delta: Float): Unit = {
    shapeBatch.begin(ShapeMode.FILLED)
    shapeBatch.rectangle(0, 20, 0, 20, height-100-20, Colors.red)
    shapeBatch.rectangle(width-20, 20, 0, 20, height-100-20, Colors.red)

    shapeBatch.rectangle(0, 0, 0, width, 20)
    shapeBatch.rectangle(0, height-20-100, 0, width, 20)

    shapeBatch.rectangle(width/2-2, 0, 0, 4, height)

    // Render players
    val segs = 12
    renderEntity(player, delta)
    renderEntity(enemy, delta)
    renderEntity(ball, delta)
    shapeBatch.end()

    shapeBatch.begin(ShapeMode.LINES)
    for(i <- 0 to 5) // TODO: Custom line width for circles
      shapeBatch.circle(width/2, height/2-50, 0, 25f-i/2f, segs, Colors.niceWhite)

    shapeBatch.end()

    font.renderString(engine.timer.getUPS + " UPS", 0, engine.getDisplayHeight()-font.font.getHeight('A')*2, 0,Colors.niceWhite)
    font.renderString(engine.timer.getFPS + " FPS", 0, engine.getDisplayHeight()-font.font.getHeight('A'), 0,Colors.niceWhite)

  }

  def renderEntity(paddle: Entity, delta: Float) = {
    val pos = paddle.getComponent(classOf[PositionComponent]).position
    val aabb = paddle.getComponent(classOf[AABBComponent]).box
    shapeBatch.rectangle(pos.x, pos.y, 0, aabb.getW, aabb.getH, Colors.niceWhite) // TODO: have access to x/y/w/h of AABB publicly
  }
}
