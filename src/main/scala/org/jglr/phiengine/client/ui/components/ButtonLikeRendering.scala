package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.render.Colors
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.render.nanovg.NanoCanvas
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.{ComponentTextures, UIComponent, UITextures, ComponentState}

abstract class ButtonLikeRendering(fontRenderer: FontRenderer) extends UIComponent(fontRenderer) {

  protected val textures: ComponentTextures
  var tileWidth: Float = 8f
  var tileHeight: Float = 8f
  var drawBackground: Boolean = true

  override def renderSelf(delta: Float, batch: SpriteBatch, canvas: NanoCanvas): Unit = {
    val state = if(isEnabled) this.state else ComponentState.DISABLED
    val prevTexture = batch.getCurrentTexture
    if(drawBackground) {
      /*val sw = tileWidth
      val sh = tileHeight
      batch.setTexture(UITextures)
      var i1: Float = sw
      var j1: Float = sh
      while (j1 < h - sh) {
        while (i1 < w - sw) {
          batch.draw(textures.get("center", state), x + i1, y + j1, z, sw, sh, Colors.white)
          i1 += sw
        }
        i1 = sw
        j1 += sh
      }

      var i: Float = x + sw
      while (i <= x + w - sw) {
        batch.draw(textures.get("south", state), i, y, z, sw, sh, Colors.white)
        batch.draw(textures.get("north", state), i, y + h - sh, z, sw, sh, Colors.white)
        i += sw
      }

      var j: Float = y + sh
      while (j <= y + h - sh) {
        batch.draw(textures.get("west", state), x, j, z, sw, sh, Colors.white)
        batch.draw(textures.get("east", state), x + w - sw, j, z, sw, sh, Colors.white)
        j += sh
      }

      batch.draw(textures.get("bleft", state), x, y, z, sw, sh, Colors.white)
      batch.draw(textures.get("bright", state), x + w - sw, y, z, sw, sh, Colors.white)
      batch.draw(textures.get("tleft", state), x, y + h - sh, z, sw, sh, Colors.white)
      batch.draw(textures.get("tright", state), x + w - sw, y + h - sh, z, sw, sh, Colors.white)*/
      canvas.startPath()
        canvas.fillColor(Colors.lightGray)
        canvas.strokeColor(Colors.darkGray.darker())
        canvas.roundedRect(x, y, w, h, 5)
        canvas.fill()
        canvas.strokeWidth(2f)
        canvas.stroke()
      canvas.endPath()
    }
    batch.setTexture(prevTexture)
  }
}
