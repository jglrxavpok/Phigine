package org.jglr.phiengine.client.ui

import java.util.{ArrayList, List}

import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.core.maths.Vec2
import org.jglr.phiengine.core.utils.JavaConversions._
import scala.collection.JavaConversions._

abstract class UIComponent(fontRenderer: FontRenderer) {

  val chidren: List[UIComponent] = new ArrayList[UIComponent]
  val layout: UILayout = null
  var x: Float = 0
  var y: Float = 0
  var z: Float = 0
  var w: Float
  var h: Float
  var minSize: Vec2 = new Vec2(10,10)

  def addChild(child: UIComponent): Unit = {
    chidren.add(child)
    if(layout != null)
      layout.onComponentAdded(child)
  }

  def removeChild(child: UIComponent): Unit = {
    if(chidren.remove(child) && layout != null)
    layout.onComponentRemoved(child)
  }

  def pack(): Unit = {
    chidren.forEach((c: UIComponent) => c.pack())
    if(layout == null) {
      if(chidren.isEmpty) {
        w = minSize.x
        h = minSize.y
      } else {
        var minX = Float.PositiveInfinity
        var maxX = Float.NegativeInfinity
        var minY = Float.PositiveInfinity
        var maxY = Float.NegativeInfinity
        for(c <- chidren) {
          if(c.x < minX)
          minX = c.x

          if(c.y < minY)
          minY = c.y

          if(c.x+c.w > maxX)
          maxX = c.x

          if(c.y+c.h > maxY)
          maxY = c.y+c.h
        }

        w = Math.max(minSize.x, maxX-minX)
        h = Math.max(minSize.y, maxY-minY)
      }
    } else {
      val size = layout.pack()
      w = size._1
      h = size._2
    }
  }

  def render(delta: Float): Unit = {
    chidren.forEach((c: UIComponent) => c.render(delta))
  }

  def update(delta: Float): Unit = {
    chidren.forEach((c: UIComponent) => c.update(delta))
  }
}
