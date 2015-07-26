package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.render.{Color, Colors}
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.client.ui.UIComponent

class UILabel(fontRenderer: FontRenderer, _text: String = "") extends UIComponent(fontRenderer) {

  private var text: String = _text
  var color: Color = Colors.niceWhite
  var scale: Float = 1f

  override def addChild(child: UIComponent): Unit = {
    throw new UnsupportedOperationException("UILabels can't have child components!")
  }

  override def removeChild(child: UIComponent): Unit = {
    throw new UnsupportedOperationException("UILabels can't have child components!")
  }

  def setText(newText: String) = {
    text = newText
  }

  override def renderSelf(delta: Float, batch: SpriteBatch) = {
    fontRenderer.renderString(text, x, y, z, color, scale)
  }

  override def pack(): Unit = {
    w = fontRenderer.font.getWidth(text)
    h = fontRenderer.font.getHeight(text)
  }

  pack()
}
