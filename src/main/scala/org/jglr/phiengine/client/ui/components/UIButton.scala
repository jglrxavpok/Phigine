package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.input.{ButtonMappings, Controller}
import org.jglr.phiengine.client.render.Colors
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.{UITextures, ComponentState, ComponentTextures, UIComponent}
import org.jglr.phiengine.client.ui.layouts.{FlowLayout, RelativeLayout}

object ButtonTextures extends ComponentTextures("button_") {

  def genTextures(): Unit = {
    val texts = Array("tleft", "bleft", "tright", "bright", "north", "east", "west", "south", "center")
    val states = Array((ComponentState.IDLE, ""), (ComponentState.FOCUSED, "_focused"), (ComponentState.HOVERED, "_hovered"),
      (ComponentState.DISABLED, "_disabled"))
    for(text <- texts) {
      for(state <- states) {
        register(text, state._1, state._2)
      }
    }
  }

}

class UIButton(fontRenderer: FontRenderer, text: String = null) extends UIComponent(fontRenderer) {

  layout = new FlowLayout(this,5f,5f,FlowLayout.CENTER)
  var tileWidth: Float = 8f
  var tileHeight: Float = 8f
  var drawBackground: Boolean = true
  //layout = new RelativeLayout(this)
  margins.x = 2.5f
  margins.y = 2.5f

  if(text != null) {
    val label = new UILabel(fontRenderer, text)
    label.color = Colors.white//Colors.lightGray lighter()
    addChild(label)
    pack()
  }

  override def renderSelf(delta: Float, batch: SpriteBatch): Unit = {
    val state = if(isEnabled) this.state else ComponentState.DISABLED
    if(drawBackground) {
      val sw = tileWidth
      val sh = tileHeight
      batch.setTexture(UITextures)
      var i1: Float = sw
      var j1: Float = sh
      while (j1 < h - sh) {
        while (i1 < w - sw) {
          batch.draw(ButtonTextures.get("center", state), x + i1, y + j1, z, sw, sh, Colors.white)
          i1 += sw
        }
        i1 = sw
        j1 += sh
      }

      var i: Float = x + sw
      while (i <= x + w - sw) {
        batch.draw(ButtonTextures.get("south", state), i, y, z, sw, sh, Colors.white)
        batch.draw(ButtonTextures.get("north", state), i, y + h - sh, z, sw, sh, Colors.white)
        i += sw
      }

      var j: Float = y + sh
      while (j <= y + h - sh) {
        batch.draw(ButtonTextures.get("west", state), x, j, z, sw, sh, Colors.white)
        batch.draw(ButtonTextures.get("east", state), x + w - sw, j, z, sw, sh, Colors.white)
        j += sh
      }

      batch.draw(ButtonTextures.get("bleft", state), x, y, z, sw, sh, Colors.white)
      batch.draw(ButtonTextures.get("bright", state), x + w - sw, y, z, sw, sh, Colors.white)
      batch.draw(ButtonTextures.get("tleft", state), x, y + h - sh, z, sw, sh, Colors.white)
      batch.draw(ButtonTextures.get("tright", state), x + w - sw, y + h - sh, z, sw, sh, Colors.white)
    }
  }

  override def onMoved(): Unit = layout.recalculatePositions()

  override def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean = {
    if(!isEnabled)
      return false
    if(!super.onMousePressed(screenX, screenY, button))
      if(isMouseOn(screenX, screenY, x, y, w, h)) {
        setState(ComponentState.FOCUSED)
        return true
      }
    false
  }

  override def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    if(!isEnabled)
      return false
    if(!super.onMouseReleased(screenX, screenY, button))
      if(isMouseOn(screenX, screenY, x, y, w, h)) {
        setState(ComponentState.HOVERED)
        return true
      }
    false
  }


  override def onMouseMoved(screenX: Int, screenY: Int): Boolean = {
    if(!isEnabled)
      return false
    if(isMouseOn(screenX, screenY, x, y, w, h))
      setState(ComponentState.HOVERED)
    else
      setState(ComponentState.IDLE)
    super.onMouseMoved(screenX, screenY)
  }

  override def onButtonPressed(controller: Controller, buttonCode: Int): Boolean = {
    if(!isEnabled)
      return false
    if(buttonCode == ButtonMappings.confirm && state == ComponentState.HOVERED) {
      setState(ComponentState.FOCUSED)
      return true
    }
    super.onButtonPressed(controller, buttonCode)
  }

  override def onButtonReleased(controller: Controller, buttonCode: Int): Boolean = {
    if(!isEnabled)
      return false
    if(buttonCode == ButtonMappings.confirm && state == ComponentState.FOCUSED) {
      setState(ComponentState.HOVERED)
      return true
    }
    super.onButtonReleased(controller, buttonCode)
  }

}
