package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.maths.Vec2

object TextureRegion {
  val NULL = new TextureRegion(0,0,0,0)
}

class TextureRegion(val minU: Float, val minV: Float, val maxU: Float, val maxV: Float) {

  def this(minUV: Vec2, maxUV: Vec2) {
    this(minUV.getX, minUV.getY, maxUV.getX, maxUV.getY)
  }

  def getMaxU: Float = maxU

  def getMaxV: Float = maxV

  def getMinU: Float = minU

  def getMinV: Float = minV
}