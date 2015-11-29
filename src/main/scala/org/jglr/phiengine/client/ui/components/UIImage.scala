package org.jglr.phiengine.client.ui.components

import com.google.common.base.Objects
import org.jglr.phiengine.client.render.{Colors, TextureRegion, ITexture}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.ui.UIComponent
import com.google.common.base.Preconditions._

class UIImage(var image: ITexture, var scale: Float = 1f, var region: TextureRegion = new TextureRegion(0f,0f,1f,1f)) extends UIComponent(null) {

  setImage(image, scale, region)

  def setImage(image: ITexture, scale: Float = scale, region: TextureRegion = region): Unit = {
    checkNotNull(image, "image", Array())
    checkNotNull(scale, "scale", Array())
    checkNotNull(region, "region", Array())
    this.image = image
    this.scale = scale
    this.region = region
    w = image.getWidth * scale * Math.abs(region.getMaxU - region.getMinU)
    h = image.getHeight * scale * Math.abs(region.getMaxV - region.getMinV)
    minSize.set(w, h)
  }

  def setRegion(region: TextureRegion): Unit = {
    checkNotNull(region, "region", Array())
    this.region = region
  }

  override def renderSelf(delta: Float, batch: SpriteBatch): Unit = {
    super.renderSelf(delta, batch)
    batch.setTexture(image)
    batch.draw(region, x, y, z, w, h, Colors.white)
  }
}
