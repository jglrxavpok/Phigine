package org.jglr.phiengine.client.ui

import java.util.{Map, HashMap, ArrayList, List}

import org.jglr.phiengine.client.input.PovDirection.Type
import org.jglr.phiengine.client.input.{ButtonMappings, Controller, ControllerListener, InputListener}
import org.jglr.phiengine.client.render.TextureRegion
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.core.maths.Vec2
import org.jglr.phiengine.core.utils.JavaConversions._
import org.lwjgl.glfw.GLFW
import scala.collection.JavaConversions._

object ComponentState extends Enumeration {
  type Type = Value
  val IDLE, HOVERED, FOCUSED, DISABLED = Value
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

abstract class UIComponent(fontRenderer: FontRenderer) extends InputListener with ControllerListener {

  val children: List[UIComponent] = new ArrayList[UIComponent]
  val childrenToAdd: List[UIComponent] = new ArrayList[UIComponent]
  val childrenToRemove: List[UIComponent] = new ArrayList[UIComponent]
  var layout: UILayout = null
  var x: Float = 0
  var y: Float = 0
  var z: Float = 0
  var minSize: Vec2 = new Vec2(10,10)
  var w: Float = minSize.x
  var h: Float = minSize.y
  private var selected: UIComponent = null
  var nextComponent: UIComponent = this
  var previousComponent: UIComponent = this
  var state = ComponentState.IDLE
  var time: Float = 0f
  var lastTimeAxisMoved: Float = 0f
  var margins: Vec2 = new Vec2(0,0)
  var firstSelected: UIComponent = null
  private var enabled = true
  var allowControllerNavigation = true
  private var updating = false

  def addChild(child: UIComponent, ignoreUpdating: Boolean = false): UIComponent = {
    if(!ignoreUpdating || !updating) {
      if (children.add(child) && layout != null)
        layout.onComponentAdded(child)
    } else {
      childrenToAdd.add(child)
    }
    this
  }

  def enable(): Unit = {
    enabled = true
  }

  def disable(): Unit = {
    enabled = false
  }

  def isEnabled: Boolean = {
    enabled
  }

  def removeChild(child: UIComponent, ignoreUpdating: Boolean = false): UIComponent = {
    if(!ignoreUpdating || !updating) {
      if(children.remove(child) && layout != null)
        layout.onComponentRemoved(child)
    } else {
      childrenToRemove.add(child)
    }
    this
  }

  def setState(newState: ComponentState.Type): UIComponent = {
    onStateChanged(state, newState)
    state = newState
    this
  }

  def onStateChanged(oldState: ComponentState.Type, newState: ComponentState.Type): Unit = {

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

  def updateSelf(delta: Float) = {
    time += delta
    if(selected == null && firstSelected != null) {
      selected = firstSelected
      selected.setState(ComponentState.HOVERED)
    }

    while(!childrenToAdd.isEmpty) {
      val child = childrenToAdd.remove(0)
      addChild(child, true)
    }

    while(!childrenToRemove.isEmpty) {
      val child = childrenToRemove.remove(0)
      removeChild(child, true)
    }
  }

  def renderSelf(delta: Float, batch: SpriteBatch) = {}

  def update(delta: Float): Unit = {
    updating = true
    updateSelf(delta)
    children.forEach((c: UIComponent) => c.update(delta))
    updating = false
  }

  override def onKeyPressed(keycode: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onKeyPressed(keycode))
        return true
    }
    false
  }

  override def onKeyReleased(keycode: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onKeyReleased(keycode))
        return true
    }
    false
  }

  override def onMouseMoved(screenX: Int, screenY: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onMouseMoved(screenX, screenY))
        return true
    }
    false
  }

  override def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean = {
    if(!isEnabled)
      return false
    selected = null
    for(c <- children) {
      if (c.onMousePressed(screenX, screenY, button) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        selected = c
        return true
      }
    }
    false
  }

  override def onKeyTyped(character: Char): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onKeyTyped(character))
        return true
    }
    false
  }

  override def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onMouseReleased(screenX, screenY, button) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        if(selected == c) {
          onComponentClicked(selected)
        }
        return true
      }
    }
    selected = null
    false
  }

  override def onScroll(dir: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onScroll(dir))
        return true
    }
    false
  }

  override def onConnection(controller: Controller): Unit = {
    if(!isEnabled)
      return
    children.forEach((c: UIComponent) => c.onConnection(controller))
  }

  override def onPovMoved(controller: Controller, povCode: Int, value: Type): Boolean = {
    if(!isEnabled)
    return false
    for(c <- children) {
      if(c.onPovMoved(controller, povCode, value))
      return true
    }
    false
  }

  override def onAxisMoved(controller: Controller, axisCode: Int, value: Float): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onAxisMoved(controller, axisCode, value))
        return true
    }
    if(!allowControllerNavigation)
      return false
    if(Math.abs(value) < 0.25f || time-lastTimeAxisMoved < 0.65f)
      return false
    lastTimeAxisMoved = time
    if(!children.isEmpty && axisCode == ButtonMappings.yLeftAxis) {
      val dir = Math.signum(value)
      val origin =
        if(selected == null)
          if(firstSelected != null)
            firstSelected
          else
            children(0)
        else
          selected
      val newSelected: UIComponent =
        dir match {
            case 1f =>
              origin.nextComponent
            case -1f =>
              origin.previousComponent

            case _ =>
              null
        }
      if(newSelected != null) {
        if(selected != null) {
          selected.setState(ComponentState.IDLE)
        }
        selected = newSelected
        selected.setState(ComponentState.HOVERED)
      }
    }
    false
  }

  override def onButtonPressed(controller: Controller, buttonCode: Int): Boolean = {
    if(!isEnabled)
      return false
    selected = null
    for(c <- children) {
      if (c.onButtonPressed(controller, buttonCode) && buttonCode == ButtonMappings.confirm) {
        if(allowControllerNavigation)
          selected = c
        return true
      }
    }
    false
  }

  override def onDisconnection(controller: Controller): Unit = {
    if(!isEnabled)
      return
    children.forEach((c: UIComponent) => c.onDisconnection(controller))
  }

  override def onButtonReleased(controller: Controller, buttonCode: Int): Boolean = {
    if(!isEnabled)
      return false
    for(c <- children) {
      if(c.onButtonReleased(controller, buttonCode) && buttonCode == ButtonMappings.confirm) {
        if(allowControllerNavigation && selected == c) {
          onComponentClicked(c)
        }
        return true
      }
    }
    selected = null
    false
  }

  def onComponentClicked(comp: UIComponent): Unit = {}

  def isMouseOn(mx: Float, my: Float, x: Float, y: Float, w: Float, h: Float): Boolean = {
    (mx > x && mx <= x+w) && (my > y && my <= y+h)
  }

  def isHovered(mx: Float, my: Float): Boolean = isMouseOn(mx, my, x, y, w, h)

  override def toString: String = {
    if(children.isEmpty) {
      "Empty"
    } else {
      val builder = new StringBuilder()
      var first = true
      for(c <- children) {
        if(!first)
          builder.append(", ")
        first = false
        builder.append(c.toString)
      }
      builder.toString()
    }
  }
}
