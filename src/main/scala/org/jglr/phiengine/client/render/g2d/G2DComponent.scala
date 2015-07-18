package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.core.entity.{Component, Entity}

abstract class G2DComponent[A<:Batch](entity: Entity) extends Component(entity) {
  def render(batch: A, delta: Float): Unit
}
