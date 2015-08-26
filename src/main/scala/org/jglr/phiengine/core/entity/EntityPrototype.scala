package org.jglr.phiengine.core.entity

/**
 * An EntityPrototype is a collection of components (either in class form or created at entity initialisation), used to quickly create
 * a special type of [[org.jglr.phiengine.core.entity.Entity Entity]].
 * A prototype can be shared across multiple Entity instances and '''''should not be used as a way to determine the behavior of the entity'''''
 */
abstract class EntityPrototype extends Traversable[CompClass] with Iterable[CompClass] {

  /**
   * Provides a sequence of component classes defining this prototype
   * @return
   *         The sequence of component classes
   */
  def staticComponents: Seq[CompClass]

  /**
   * Adds entity-dependent or dynamic components to `target`
   * @param target
   *               The entity to which add the components
   */
  def handleDynamicComponents(target: Entity): Unit = {}

  /**
   * Adds the components this prototype holds inside `target` in the following order:<br/>
   * - Static components, if any<br/>
   * - Dynamic components, if any
   * @param target
   *               The entity to which add the components
   */
  def apply(target: Entity): Unit = {
    for(comp <- this) {
      target.addComponent(comp)
    }
    handleDynamicComponents(target)
  }

  /**
   * Creates a new Iterator object going through `staticComponents`.
   * This method is intended to be used with foreach loops:
   * {{{
   *   for(comp <- prototype) {
   *     // handle comp
   *   }
   * }}}
   * @return
   *         The created Iterator instance
   */
  final override def iterator: Iterator[CompClass] = staticComponents.iterator
}

object EmptyPrototype extends EntityPrototype {
  override def staticComponents: Seq[CompClass] = {
    Nil
  }
}