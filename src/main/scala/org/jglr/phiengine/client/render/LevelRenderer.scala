package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.render.g2d._
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.utils.JavaConversions._

class LevelRenderer(level: Level) {

  val spriteBatch: SpriteBatch = new SpriteBatch(200)
  val shapeBatch: ShapeBatch = new ShapeBatch()

  def render(delta: Float): Unit = {
    shapeBatch.begin()
    spriteBatch.begin()
    level.components(classOf[G2DComponent[_]])
      .forEach((c: G2DComponent[_]) => {
        c match {
          case comp: SpriteComponent =>
            comp.render(spriteBatch, delta)
          case comp: ShapeComponent =>
            comp.render(shapeBatch, delta)
          case _ =>
        }
      })
    shapeBatch.end()
    spriteBatch.end()
  }
}
