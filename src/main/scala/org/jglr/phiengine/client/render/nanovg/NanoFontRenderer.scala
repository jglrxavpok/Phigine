package org.jglr.phiengine.client.render.nanovg

import org.jglr.phiengine.client.render.Color
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{Font, FontRenderer}

class NanoFontRenderer(supportedChars: Array[Char], val canvas: NanoCanvas, var nanoFont: NVGFont) extends FontRenderer(supportedChars) {

  def this(supportedChars: Array[Char], canvas: NanoCanvas, nanoFont: Font) {
    this(supportedChars, canvas, canvas.createFont(nanoFont.javaFont.getName, nanoFont.javaFont.getFontName))
  }

  override def renderString(string: String, x: Float, y: Float, z: Float, color: Color, scale: Float = 1f, batch: SpriteBatch = null) = {
    if(batch != null) {
      throw new IllegalArgumentException("NanoFontRenderer can't render to a SpriteBatch")
    }
    canvas.saveState()
    canvas.font(nanoFont)
    canvas.scale(scale)
    canvas.fillColor(color)
    canvas.drawString(string, x, y)
    canvas.restoreState()
  }

  override def getWidth(text: String): Float = {
    canvas.fontWidth(text)
  }

  override def getHeight(text: String): Float = {
    canvas.fontHeight(text)
  }
}
