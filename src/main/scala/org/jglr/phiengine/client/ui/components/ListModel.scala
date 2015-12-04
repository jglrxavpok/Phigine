package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent

trait ListModel {

  def createRepresentation(fontRenderer: FontRenderer, value: Any): UIComponent
}
