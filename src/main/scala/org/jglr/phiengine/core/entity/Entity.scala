package org.jglr.phiengine.core.entity

import java.util.{ArrayList, List}

import org.jglr.phiengine.client.render.g2d.{SpriteBatch, SpriteComponent}
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.utils.JavaConversions._

class Entity(val level: Level) {

  val components: List[Component] = new ArrayList

  def has[T<:Component](compClass: Class[T]) = {
    var result = false
    components.forEach((c: Component) => {
      if(compClass.isAssignableFrom(c.getClass)) {
        result = true
      }
    })
    result
  }

  def getLevel: Level = {
    level
  }

  private def createComponent[A<:Component](comp: Class[A]): A = {
    val cons = comp.getConstructor(classOf[Entity])
    cons.newInstance(this)
  }

  def addComponent[A<:Component](comp: Class[A]): A = {
    val component = createComponent(comp)
    components.add(component)
    component
  }

  def getComponents[A<:Component](compClass: Class[A]): List[A] = {
    val result = new ArrayList[A]
    components.stream()
      .filter((c: Component) => compClass.isAssignableFrom(c.getClass))
      .forEach((c: Component) => result.add(c.asInstanceOf[A]))
    result
  }

  def getComponent[A <: Component](compClass: Class[A]): A = {
    getComponents(compClass).get(0)
  }
}