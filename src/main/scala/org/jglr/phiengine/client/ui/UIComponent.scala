package org.jglr.phiengine.client.ui

import java.util.{Map, HashMap, ArrayList, List}

import org.jglr.phiengine.client.render.TextureRegion
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.core.maths.Vec2
import org.jglr.phiengine.core.utils.JavaConversions._
import scala.collection.JavaConversions._

object ComponentState extends Enumeration {
  type Type = Value
  val IDLE, HOVERED, FOCUSED = Value
}

abstract class ComponentTextures(val prefix: String) {
  val texMap = new HashMap[ComponentState.Type, Map[String, TextureRegion]]

  def genTextures(): Unit

  protected def register(name: String, state: ComponentState.Type, suffix: String = ""): TextureRegion = {
    if(!texMap.containsKey(state)) {
      texMap.put(state, new HashMap[String, TextureRegion])
    }
    val generated = UITextures.generateIcon(prefix+name+suffix)
    texMap.get(state).put(name, generated)
  }

  def get(name: String, state: ComponentState.Type): TextureRegion = {
    val res = texMap.get(state).get(name)
    if(res == null || res.isNull) {
      texMap.get(ComponentState.IDLE).get(name)
    } else {
      res
    }
  }
}

abstract class UIComponent(fontRenderer: FontRenderer) {

  val children: List[UIComponent] = new ArrayList[UIComponent]
  var layout: UILayout = null
  var x: Float = 0
  var y: Float = 0
  var z: Float = 0
  var minSize: Vec2 = new Vec2(10,10)
  var w: Float = minSize.x
  var h: Float = minSize.y

  def addChild(child: UIComponent): Unit = {
    if(children.add(child) && layout != null)
      layout.onComponentAdded(child)
  }

  def removeChild(child: UIComponent): Unit = {
    if(children.remove(child) && layout != null)
      layout.onComponentRemoved(child)
  }

  def onMoved(): Unit = {}

  def pack(): Unit = {
    children.forEach((c: UIComponent) => c.pack())
    if(layout == null) {
      if(children.isEmpty) {
        w = minSize.x
        h = minSize.y
      } else {
        var minX = Float.PositiveInfinity
        var maxX = Float.NegativeInfinity
        var minY = Float.PositiveInfinity
        var maxY = Float.NegativeInfinity
        for(c <- children) {
          if(c.x < minX)
            minX = c.x

          if(c.y < minY)
            minY = c.y

          if(c.x+c.w > maxX)
            maxX = c.x+c.w

          if(c.y+c.h > maxY)
            maxY = c.y+c.h
        }
        w = Math.max(minSize.x, maxX-minX)
        h = Math.max(minSize.y, maxY-minY)
      }
    } else {
      layout.recalculatePositions()
      val size = layout.pack()
      w = Math.max(minSize.x, size.x)
      h = Math.max(minSize.y, size.y)
    }
  }

  def render(delta: Float, batch: SpriteBatch): Unit = {
    renderSelf(delta, batch)
    children.forEach((c: UIComponent) => c.render(delta, batch))
  }

  def updateSelf(delta: Float) = {}

  def renderSelf(delta: Float, batch: SpriteBatch) = {}

  def update(delta: Float): Unit = {
    updateSelf(delta)
    children.forEach((c: UIComponent) => c.update(delta))
  }
}
