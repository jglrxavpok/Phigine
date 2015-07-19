package org.jglr.phiengine.client.ui

import java.util.{List, ArrayList}

import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.core.PhiEngine

class UI(var fontRenderer: FontRenderer = null) extends UIComponent(fontRenderer) {

  if(fontRenderer == null) {
    fontRenderer = new FontRenderer(FontRenderer.ASCII, Font.get("Arial", 28))
  }

  val batch = new SpriteBatch()

  override def render(delta: Float): Unit = {
    batch.begin()
    //batch.setTexture()
    super.render(delta)
    batch.end()
  }

  override var w: Float = PhiEngine.getInstance().getDisplayWidth()
  override var h: Float = PhiEngine.getInstance().getDisplayWidth()
}
