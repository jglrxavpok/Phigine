import org.jglr.phiengine.client.render.Texture
import org.jglr.phiengine.client.render.g2d.{SpriteBatch, SpriteComponent}
import org.jglr.phiengine.core.entity.Entity

class TestComponent(entity: Entity) extends SpriteComponent(entity) {

  val logo = new Texture("logo.png")

  override def render(batch: SpriteBatch, delta: Float): Unit = {
    batch.draw(logo, 100, 100)
  }
}
