package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.input.{ButtonMappings, Controller}
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.ComponentState

class UIFunctionButton(fontRenderer: FontRenderer, name: String, val func: () => Unit) extends UIButton(fontRenderer, name) {

  protected var futureFunc: () => Unit = null

  protected def this(fontRenderer: FontRenderer, name: String) {
    this(fontRenderer, name, null)
  }

  override def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    if(!isEnabled)
      return false
    if(super.onMouseReleased(screenX, screenY, button)) {
      if(func == null) {
        if(futureFunc != null) {
          futureFunc.apply()
        }
      } else {
        func.apply()
      }
      return true
    }
    false
  }

  override def onButtonReleased(controller: Controller, buttonCode: Int): Boolean = {
    if(!isEnabled)
      return false
    if(buttonCode == ButtonMappings.confirm && state == ComponentState.FOCUSED) {
      func.apply()
    }
    super.onButtonReleased(controller, buttonCode)
  }

}
