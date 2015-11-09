package org.jglr.phiengine.core.entity.components

import org.jglr.phiengine.core.entity.{Entity, Component}

abstract class OnLandingComponent(entity: Entity) extends Component(entity) {

  protected val posComp = entity.getComponent(classOf[PositionComponent])

  def onLandingX(velX: Float): Unit

  def onLandingY(velY: Float): Unit
}
