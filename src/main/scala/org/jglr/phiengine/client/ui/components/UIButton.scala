package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.layouts.RelativeLayout

class UIButton(fontRenderer: FontRenderer, text: String = null) extends UIComponent(fontRenderer) {

  layout = new RelativeLayout(this)

  if(text != null) {
    addChild(new UILabel(fontRenderer, text))
    pack()
    println(s"$w, $h")
  }

  override def onMoved(): Unit = layout.recalculatePositions()
}
