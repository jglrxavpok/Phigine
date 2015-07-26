package org.jglr.phiengine.client.ui

import java.util.HashMap

import org.jglr.phiengine.core.maths.Vec2
import scala.collection.JavaConversions._

abstract class UILayout(comp: UIComponent) {

  private val positions = new HashMap[UIComponent, Vec2]

  def onComponentAdded(added: UIComponent): Unit = {
    positions.put(added, new Vec2(added.x, added.y))
  }

  def onComponentRemoved(removed: UIComponent): Unit = {
    positions.remove(removed)
  }

  def pack(): Vec2 = {
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

    new Vec2(maxX-minX, maxY-minY)
  }

  def recalculatePositions(): Unit

  def getPosition(comp: UIComponent): Vec2 = {
    positions.get(comp)
  }

  def setPosition(comp: UIComponent, pos: Vec2): Unit = {
    getPosition(comp).set(pos)
  }
}
