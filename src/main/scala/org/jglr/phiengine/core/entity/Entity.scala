package org.jglr.phiengine.core.entity

import java.util.{ArrayList, List}

import org.jglr.phiengine.client.render.g2d.{SpriteBatch, SpriteComponent}
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.utils.JavaConversions._

/**
 * An entity is a collection of [[org.jglr.phiengine.core.entity.Component Components]] interacting with one another to define the behavior of said entity.
 * The Entity class provides ways to add and remove components at runtime.
 * It evolves in a [[org.jglr.phiengine.core.level.Level Level]].
 * An entity can be created from [[org.jglr.phiengine.core.level.Level Level]]'s `createEntity` method or directly via the constructor.
 *
 * @param level
 *              The level in which the entity evolves
 */
class Entity(val level: Level) {

  /**
   * A list of the components of this entity.
   * For more info about components, go see [[org.jglr.phiengine.core.entity.Component Component]]
   */
  val components: List[Component] = new ArrayList

  /**
   * The prototype used to create the entity.
   * Might be null and *should not* be used to interact with the entity in any way
   */
  var protype: EntityPrototype = null

  /**
   * Check if the entity is still contained in the level
   * @return true if the entity is still in the level
   */
  def isActive: Boolean = {
    level.hasEntity(this)
  }

  /**
   * Checks if the entity contains the given component
   * @param compClass
   *                  The component's class to check
   * @return
   *         true if the entity contains a component of class compClass
   */
  def has(compClass: CompClass) = {
    var result = false
    components.forEach((c: Component) => {
      if(compClass.isAssignableFrom(c.getClass)) {
        result = true
      }
    })
    result
  }

  /**
   * Executes a function for each instance of component that the entity contains.
   * Example implementation:
   * {{{
   * if(has(component)
   *   getComponents(component).forEach(function)
   * }}}
   * @param component
   *                  The component's class to check the presence of
   * @param function
   *                 The function to execute, taking a parameter being the instance of component and whose return type is ignored
   * @tparam T
   *           The component type
   */
  def ifHas[T<:Component](component: Class[T])(function: (T => Any)): Unit = {
    if(has(component))
      getComponents(component).forEach(function)
  }

  /**
   * The level used to create the entity
   * @return
   *         The level instance
   */
  def getLevel: Level = {
    level
  }

  private def createComponent[A<:Component](comp: Class[A]): A = {
    val cons = comp.getConstructor(classOf[Entity])
    cons.newInstance(this)
  }

  /**
   * Adds a component to the entity.
   * The component is created from the given class with the constructor Component(e: Entity) and this Entity instance as a parameter.
   *
   *
   * '''Please note that this method fails with components that do not provide a constructor matching [[org.jglr.phiengine.core.entity.Component Component]]'s'''
   * @param comp
   *             The component to add class
   * @tparam A
   *           The component type
   * @return
   *         The created component, for chaining
   */
  def addComponent[A<:Component](comp: Class[A]): A = {
    addComponent(createComponent(comp))
  }

  /**
   * Adds a pre-built component to the entity.
   * @param comp
   *             The component to add to the entity
   * @tparam A
   *           The type of the component
   * @return
   *         The given component, for chaining
   */
  def addComponent[A<:Component](comp: A): A = {
    components.add(comp)
    comp
  }

  /**
   * Returns a list of the instances of compClass containing inside this entity
   * @param compClass
   *                  The component class
   * @tparam A
   *           The type of the component
   * @return
   *         A list with all the instances of the compClass
   */
  def getComponents[A<:Component](compClass: Class[A]): List[A] = {
    val result = new ArrayList[A]
    components.stream()
      .filter((c: Component) => compClass.isAssignableFrom(c.getClass))
      .forEach((c: Component) => result.add(c.asInstanceOf[A]))
    result
  }

  /**
   * Returns the first instance of compClass found inside the entity.
   * Uses getComponents(compClass) to fetch the list of components first and then retrieves the first element
   * @param compClass
   *                  The component class
   * @tparam A
   *           The component type
   * @return
   *         The first component found matching
   */
  def getComponent[A <: Component](compClass: Class[A]): A = {
    getComponents(compClass).get(0)
  }

  /**
   * Removes a component from the entity
   * @param comp
   *             The component to remove
   * @return
   *         true if the component was inside the entity and has been removed
   */
  def removeComponent(comp: Component): Boolean = {
    components.remove(comp)
  }
}