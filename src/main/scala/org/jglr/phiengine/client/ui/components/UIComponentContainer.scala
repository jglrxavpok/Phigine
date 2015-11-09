package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.components.UIPanel
import org.jglr.phiengine.client.ui.layouts.RelativeLayout

class UIComponentContainer(component: UIComponent) extends UIPanel(null) {

  layout = new RelativeLayout(this)
  addChild(component)

  w = component.w
  h = component.h
  minSize = component.minSize

  override def pack(): Unit = {
  }
}
