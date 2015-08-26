package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.render.g2d._
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.utils.JavaConversions._

/**
 * Helper class providing a fast way to render a level via both a [[org.jglr.phiengine.client.render.g2d.SpriteBatch SpriteBatch]] and a [[org.jglr.phiengine.client.render.g2d.ShapeBatch ShapeBatch]].
 * Only renders entities in the base implementation
 * @param level
 *              The level to render
 */
class LevelRenderer(level: Level) {

  val spriteBatch: SpriteBatch = new SpriteBatch(200)
  val shapeBatch: ShapeBatch = new ShapeBatch()

  /**
   * Renders the level
   * @param delta
   *              Delta time between the last two frames
   */
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
