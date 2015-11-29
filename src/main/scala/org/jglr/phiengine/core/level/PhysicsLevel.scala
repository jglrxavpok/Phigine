package org.jglr.phiengine.core.level

import org.jbox2d.dynamics.World
import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.utils.Box2DUtils._
import org.joml.Vector2f

class PhysicsLevel(gravity: Vector2f) extends Level {
  val world = new World(gravity)

  override def updateLevel(delta: Float): Unit = {
    world.step(delta, 3, 8)
  }

  override def onEntityDespawn(entity: Entity): Unit = {

  }

  override def onEntitySpawn(entity: Entity): Unit = {

  }

  override def onEntityCreation(entity: Entity): Unit = {

  }
}
