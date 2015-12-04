package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent
import java.util
import org.jglr.phiengine.core.utils.JavaConversions._

import org.jglr.phiengine.client.ui.layouts.{AlignDirection, AlignedLayout}

class UIList[T](fontRenderer: FontRenderer, data: util.List[T]) extends UIPanel(fontRenderer) {

  var model = DefaultListModel
  layout = new AlignedLayout(this, AlignDirection.TopToBottom)
  setData(data)

  def setData(data: util.List[T]): Unit = {
    children.forEach((comp: UIComponent) => removeChild(comp))
    data.forEach((value: T) => addChild(model.createRepresentation(fontRenderer, value)))
  }

}
