import org.jglr.phiengine.client.render.Texture
import org.jglr.phiengine.client.render.g2d.{Sprite, Skeleton}

class TestSkeleton() extends Skeleton {

  val body = createChild(0,0)
  body.sprite = new Sprite(new Texture("icon64.png"))
}
