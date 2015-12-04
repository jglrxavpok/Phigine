package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.input.{ButtonMappings, Controller}
import org.jglr.phiengine.client.render.Colors
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.layouts.FlowLayout
import org.jglr.phiengine.client.ui.{ComponentTextures, ComponentState, UIComponent, UITextures}

object ToggleButtonTextures extends ComponentTextures("button_") {

  def genTextures(): Unit = {
    val texts = Array("tleft", "bleft", "tright", "bright", "north", "east", "west", "south", "center")
    val states = Array((ComponentState.OFF, ""), (ComponentState.ON, "_on"), (ComponentState.DISABLED, "_disabled"))
    for(text <- texts) {
      for(state <- states) {
        register(text, state._1, state._2)
      }
    }
  }

}

class UIToggleButton(fontRenderer: FontRenderer, text: String, defaultValue: Boolean = false) extends UIFunctionButton(fontRenderer, text) {

  private var toggleState: Boolean = defaultValue

  futureFunc = () => switchState()
  setState(if(toggleState) ComponentState.ON else ComponentState.OFF)

  def switchState(): Unit = {
    toggleState = !toggleState
    setState(if(toggleState) ComponentState.ON else ComponentState.OFF)
  }


  def getToggleState: Boolean = toggleState

  override protected val textures: ComponentTextures = ToggleButtonTextures


}
