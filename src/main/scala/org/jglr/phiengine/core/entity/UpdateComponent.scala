package org.jglr.phiengine.core.entity

abstract class UpdateComponent(entity: Entity) extends Component(entity) {
  def update(delta: Float): Unit

  def postUpdate(delta: Float): Unit = {}

  def preUpdate(delta: Float): Unit = {}
}
