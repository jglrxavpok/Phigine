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

class UIButton(fontRenderer: FontRenderer, text: String = null) extends ButtonLikeRendering(fontRenderer) {

  layout = new FlowLayout(this,5f,5f,FlowLayout.CENTER)
  //layout = new RelativeLayout(this)
  margins.x = 2.5f
  margins.y = 2.5f

  if(text != null) {
    val label = new UILabel(fontRenderer, text)
    label.color = Colors.white
    addChild(label)
    pack()
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

  override protected val textures: ComponentTextures = ButtonTextures
}
