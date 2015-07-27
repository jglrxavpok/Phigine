package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.render.TextureRegion
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.{UITextures, UIComponent}
import org.jglr.phiengine.client.ui.layouts.RelativeLayout

object ButtonTextures {
  var topLeftCorner: TextureRegion = null
  var bottomLeftCorner: TextureRegion = null
  var topRightCorner: TextureRegion = null
  var bottomRightCorner: TextureRegion = null
  var northEdge: TextureRegion = null
  var eastEdge: TextureRegion = null
  var westEdge: TextureRegion = null
  var southEdge: TextureRegion = null
  var center: TextureRegion = null

  def genTextures() = {
    topLeftCorner = UITextures.generateIcon("button_tleft")
    bottomLeftCorner = UITextures.generateIcon("button_bleft")
    topRightCorner = UITextures.generateIcon("button_tright")
    bottomRightCorner = UITextures.generateIcon("button_bright")
    northEdge = UITextures.generateIcon("button_north")
    eastEdge = UITextures.generateIcon("button_east")
    westEdge = UITextures.generateIcon("button_west")
    southEdge = UITextures.generateIcon("button_south")
    center = UITextures.generateIcon("button_center")
  }

}

class UIButton(fontRenderer: FontRenderer, text: String = null) extends UIComponent(fontRenderer) {

  layout = new RelativeLayout(this)

  if(text != null) {
    addChild(new UILabel(fontRenderer, text))
    pack()
  }

  override def renderSelf(delta: Float, batch: SpriteBatch): Unit = {
    val sw = 8f
    val sh = 8f
    batch.begin()

    var i1: Float = sw
    var j1: Float = sh
    while(j1 < h-sh) {
      while(i1 < w-sw) {
        batch.draw(ButtonTextures.center, x+i1, y+j1, z, sw, sh)
        i1 += sw
      }
      i1 = sw
      j1 += sh
    }

    var i: Float = x+sw
    while(i <= x+w-sw) {
      batch.draw(ButtonTextures.southEdge, i, y, z, sw, sh)
      batch.draw(ButtonTextures.northEdge, i, y+h-sh, z, sw, sh)
      i+=sw
    }

    var j: Float = y+sh
    while(j <= y+h-sh) {
      batch.draw(ButtonTextures.westEdge, x, j, z, sw, sh)
      batch.draw(ButtonTextures.eastEdge, x+w-sw, j, z, sw, sh)
      j+=sh
    }

    batch.draw(ButtonTextures.bottomLeftCorner, x, y, z, sw, sh)
    batch.draw(ButtonTextures.bottomRightCorner, x+w-sw, y, z, sw, sh)
    batch.draw(ButtonTextures.topLeftCorner, x, y+h-sh, z, sw, sh)
    batch.draw(ButtonTextures.topRightCorner, x+w-sw, y+h-sh, z, sw, sh)
    batch.end()
  }

  override def onMoved(): Unit = layout.recalculatePositions()
}
