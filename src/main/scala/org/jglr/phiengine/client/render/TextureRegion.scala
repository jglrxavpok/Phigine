package org.jglr.phiengine.client.render

import org.joml.Vector2f

object TextureRegion {
  val NULL = new TextureRegion(0,0,0,0,true)
}

class TextureRegion(val minU: Float, val minV: Float, val maxU: Float, val maxV: Float, var isNull: Boolean = false) {

  def this(minUV: Vector2f, maxUV: Vector2f) {
    this(minUV.x, minUV.y, maxUV.x, maxUV.y)
  }

  def getMaxU: Float = maxU

  def getMaxV: Float = maxV

  def getMinU: Float = minU

  def getMinV: Float = minV
}