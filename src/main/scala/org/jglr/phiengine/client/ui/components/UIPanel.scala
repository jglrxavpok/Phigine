package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.layouts.FlowLayout

class UIPanel(val fontRenderer: FontRenderer) extends UIComponent(fontRenderer) {
  layout = new FlowLayout(this)
}
