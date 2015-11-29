package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.render.g2d.Sprite
import org.joml.Vector2f

class Spritesheet(val texture: Texture, val xFrequency: Int, val yFrequency: Int) {
  private val spriteCount: Int = xFrequency * yFrequency
  private final val regions: Array[TextureRegion] = new Array[TextureRegion](spriteCount)
  private final val sprites: Array[Sprite] = new Array[Sprite](spriteCount)

  val tileWidth: Int = texture.getWidth / xFrequency
  val tileHeight: Int = texture.getHeight / yFrequency
  for(x <- 0 until xFrequency) {
    for(y <- 0 until yFrequency) {
      val minUV: Vector2f = getCoords(x * tileWidth, y * tileHeight, texture)
      val maxUV: Vector2f = getCoords(x * tileWidth + tileWidth, y * tileHeight + tileHeight, texture)
      val region: TextureRegion = new TextureRegion(minUV, maxUV)
      regions(x + xFrequency * y) = region
      sprites(x + xFrequency * y) = new Sprite(texture, region)
    }
  }

  private def getCoords(x: Int, y: Int, texture: Texture): Vector2f = {
    val xpos: Float = x + 0.5f
    val ypos: Float = y + 0.5f
    new Vector2f(xpos / texture.getWidth.toFloat, ypos / texture.getHeight.toFloat)
  }

  def getTexture: Texture = {
    texture
  }

  def getRegions: Array[TextureRegion] = {
    regions
  }

  def getSprites: Array[Sprite] = {
    sprites
  }

  def getSpriteCount: Int = {
    spriteCount
  }
}