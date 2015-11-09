package org.jglr.phiengine.core.entity.components

import org.jglr.phiengine.core.entity.{Component, Entity}

abstract class CollisionComponent(entity: Entity) extends Component(entity) {
  def canGoTo(x: Float, y: Float, z: Float): Boolean
}
