package org.jglr.phiengine.core.entity

abstract class Component(val entity: Entity) {
  def getEntity: Entity = entity
}
