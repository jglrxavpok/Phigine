package org.jglr.phiengine.core.entity

/**
 * A component defines a part of the behavior of an entity.
 * It is preferred that subclasses use the same constructor than this class in order to allow easy instantiation by [[org.jglr.phiengine.core.entity.Entity entities]]
 * @param entity
 *               The entity on which the component will act
 */
abstract class Component(val entity: Entity)
