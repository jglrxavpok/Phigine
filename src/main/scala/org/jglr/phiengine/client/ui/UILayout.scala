package org.jglr.phiengine.client.ui

import java.util.HashMap

import org.joml.Vector2f
import scala.collection.JavaConversions._

abstract class UILayout(comp: UIComponent) {

  private val positions = new HashMap[UIComponent, Vector2f]

  def onComponentAdded(added: UIComponent): Unit = {
    resetPosition(added)
  }

  def resetPosition(comp: UIComponent) = {
    positions.put(comp, new Vector2f(comp.x, comp.y))
  }

  def onComponentRemoved(removed: UIComponent): Unit = {
    positions.remove(removed)
  }

  def pack(): Vector2f = {
    var minX = Float.PositiveInfinity
    var maxX = Float.NegativeInfinity
    var minY = Float.PositiveInfinity
    var maxY = Float.NegativeInfinity
    for(c <- comp.children) {
      if(c.x < minX)
        minX = c.x

      if(c.y < minY)
        minY = c.y

      if(c.x+c.w > maxX)
        maxX = c.x+c.w

      if(c.y+c.h > maxY)
        maxY = c.y+c.h
    }

    new Vector2f(maxX-minX + comp.margins.x*2, maxY-minY + comp.margins.y*2)
  }

  def recalculatePositions(): Unit

  def getPosition(comp: UIComponent): Vector2f = {
    positions.get(comp)
  }

  def setPosition(comp: UIComponent, pos: Vector2f): Unit = {
    getPosition(comp).set(pos)
  }
}
