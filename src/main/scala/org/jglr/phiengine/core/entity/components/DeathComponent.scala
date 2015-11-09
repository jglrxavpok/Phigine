package org.jglr.phiengine.core.entity.components

import org.jglr.phiengine.client.render.g2d.ShapeComponent
import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.utils.AutoUpdateable

abstract class DeathComponent(entity: Entity) extends ShapeComponent(entity) with AutoUpdateable {

  var shouldRender = false
  var shouldUpdate = false
  var startTime: Float = 0f
  var time: Float = 0f
  var level = entity.getLevel

  override def tick(delta: Float): Unit = {
    time += delta
  }

  def startDeath(): Unit = {
    if(!shouldUpdate) {
      shouldRender = true
      shouldUpdate = true
      startTime = time
    }
  }

}
