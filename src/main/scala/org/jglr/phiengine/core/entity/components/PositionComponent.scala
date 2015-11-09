package org.jglr.phiengine.core.entity.components

import org.jglr.phiengine.core.entity.{Entity, UpdateComponent}
import org.jglr.phiengine.core.maths.Vec3
import org.jglr.phiengine.core.utils.JavaConversions._

class PositionComponent(entity: Entity) extends UpdateComponent(entity) {
  val position: Vec3 = new Vec3
  val velocity: Vec3 = new Vec3
  val lastPosition: Vec3 = new Vec3
  var isGrounded: Boolean = false
  var wasGrounded: Boolean = false

  override def preUpdate(delta: Float): Unit = {
    lastPosition(position)
  }

  override def update(delta: Float): Unit = {
    val canGoX = entity.getComponents(classOf[CollisionComponent]).stream()
      .filter((c: CollisionComponent) => !c.canGoTo(position.x+velocity.x*delta, position.y, position.z)).count() == 0
    val canGoY = entity.getComponents(classOf[CollisionComponent]).stream()
      .filter((c: CollisionComponent) => !c.canGoTo(position.x, position.y+velocity.y*delta, position.z)).count() == 0
    val canGoY2 = entity.getComponents(classOf[CollisionComponent]).stream()
      .filter((c: CollisionComponent) => !c.canGoTo(position.x+velocity.x*delta, position.y-0.5f, position.z)).count() == 0
    val canGoZ = entity.getComponents(classOf[CollisionComponent]).stream()
      .filter((c: CollisionComponent) => !c.canGoTo(position.x, position.y, position.z+velocity.z*delta)).count() == 0

    wasGrounded = isGrounded
    isGrounded = false

    if(canGoX) {
      position.x += velocity.x*delta
    } else if(canGoY2 && wasGrounded) {
      position.x += velocity.x*delta
      position.y -= 0.5f
      isGrounded = true
      val oldVel = velocity.y
      velocity.y = 0f
      if(!wasGrounded) {
        entity.ifHas(classOf[OnLandingComponent]) {
          comp =>
            comp.onLandingY(oldVel)
        }
      }
    } else {
      val oldVel = velocity.x
      velocity.x = 0f
      entity.ifHas(classOf[OnLandingComponent]) {
        comp =>
          comp.onLandingX(oldVel)
      }
    }

    if(canGoY) {
      position.y += velocity.y*delta
    } else {
      val oldVel = velocity.y
      if(velocity.y > 0f) {
        isGrounded = true
      }
      velocity.y = 0f
      entity.ifHas(classOf[OnLandingComponent]) {
        comp =>
          comp.onLandingY(oldVel)
      }
    }

    if(canGoZ) {
      position.z += velocity.z*delta
    } else {
      velocity.z = 0f
    }
  }
}
