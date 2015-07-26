package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.{Texture, TextureRegion}
import org.jglr.phiengine.core.maths.Vec2

class Sprite(val texture: Texture, val region: TextureRegion) {
  private var width: Float = texture.getWidth * Math.abs(region.getMaxU - region.getMinU)
  private var height: Float = texture.getHeight * Math.abs(region.getMaxV - region.getMinV)
  private var x: Float = 0
  private var y: Float = 0
  private var scale: Float = 1f
  private var flipX: Boolean = false
  private var flipY: Boolean = false
  private var rotation: Float = 0
  private var zIndex: Float = 0
  private var rotationCenterX: Float = width / 2f
  private var rotationCenterY: Float = height / 2f

  def this(texture: Texture) {
    this(texture, new TextureRegion(0, 0, 1, 1))
  }

  def getX: Float = {
    x
  }

  def getY: Float = {
    y
  }

  def getWidth: Float = {
    width
  }

  def getHeight: Float = {
    height
  }

  def getScale: Float = {
    scale
  }

  def setWidth(w: Float) {
    width = w
  }

  def setHeight(h: Float) {
    height = h
  }

  def setPosition(x: Float, y: Float) {
    this.x = x
    this.y = y
  }

  def setZIndex(zIndex: Float) {
    this.zIndex = zIndex
  }

  def setScale(scale: Float) {
    this.scale = scale
  }

  def setFlip(flipX: Boolean, flipY: Boolean) {
    this.flipX = flipX
    this.flipY = flipY
  }

  def setRotation(rotation: Float) {
    this.rotation = rotation
  }

  def setRotationCenter(x: Float, y: Float) {
    rotationCenterX = x
    rotationCenterY = y
  }

  def draw(batch: SpriteBatch) {
    val bottomLeft: Vec2 = new Vec2(0, 0)
    val topLeft: Vec2 = new Vec2(0, height * scale)
    val topRight: Vec2 = new Vec2(width * scale, height * scale)
    val bottomRight: Vec2 = new Vec2(width * scale, 0)
    batch.setTexture(texture)
    val rotationCenter: Vec2 = new Vec2(rotationCenterX, rotationCenterY).*=(scale, scale)
    bottomLeft.-=(rotationCenter).rotate(rotation).+=(rotationCenter)
    topLeft.-=(rotationCenter).rotate(rotation).+=(rotationCenter)
    topRight.-=(rotationCenter).rotate(rotation).+=(rotationCenter)
    bottomRight.-=(rotationCenter).rotate(rotation).+=(rotationCenter)
    val minU: Float = if (flipX) region.getMaxU else region.getMinU
    val maxU: Float = if (flipX) region.getMinU else region.getMaxU
    val minV: Float = if (flipY) region.getMaxV else region.getMinV
    val maxV: Float = if (flipY) region.getMinV else region.getMaxV
    batch.addVertex(bottomLeft.getX + x, bottomLeft.getY + y, zIndex, minU, maxV)
    batch.addVertex(topLeft.getX + x, topLeft.getY + y, zIndex, minU, minV)
    batch.addVertex(topRight.getX + x, topRight.getY + y, zIndex, maxU, minV)
    batch.addVertex(bottomRight.getX + x, bottomRight.getY + y, zIndex, maxU, maxV)
    batch.addIndex(1)
    batch.addIndex(0)
    batch.addIndex(2)
    batch.addIndex(2)
    batch.addIndex(0)
    batch.addIndex(3)
    batch.nextSprite()
  }

  def getTexture: Texture = {
    texture
  }

  def getRegion: TextureRegion = {
    region
  }
}