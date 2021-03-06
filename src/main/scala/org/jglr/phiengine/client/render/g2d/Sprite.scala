package org.jglr.phiengine.client.render.g2d

import com.google.common.base.Preconditions._
import org.jglr.phiengine.client.render.{Texture, Colors, TextureRegion}
import org.joml.Vector2f
import org.jglr.phiengine.core.maths.VectorfExtensions._

class Sprite(var texture: Texture, var region: TextureRegion = new TextureRegion(0, 0, 1, 1)) {
  checkNotNull(texture, "texture", Array())
  checkNotNull(region, "region", Array())

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

  var color = Colors.white

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

  def getRotation: Float = rotation

  def setRotationCenter(x: Float, y: Float) {
    rotationCenterX = x
    rotationCenterY = y
  }

  def setCentered(x: Float, y: Float): Unit = {
    setPosition(x+width*scale/2f, y+height*scale/2f)
  }

  def draw(batch: SpriteBatch) {
    val bottomLeft: Vector2f = new Vector2f(0, 0)
    val topLeft: Vector2f = new Vector2f(0, height * scale)
    val topRight: Vector2f = new Vector2f(width * scale, height * scale)
    val bottomRight: Vector2f = new Vector2f(width * scale, 0)
    batch.setTexture(texture)
    val rotationCenter: Vector2f = new Vector2f(rotationCenterX, rotationCenterY).mul(scale)
    bottomLeft.sub(rotationCenter).rotate(rotation).add(rotationCenter)
    topLeft.sub(rotationCenter).rotate(rotation).add(rotationCenter)
    topRight.sub(rotationCenter).rotate(rotation).add(rotationCenter)
    bottomRight.sub(rotationCenter).rotate(rotation).add(rotationCenter)
    val minU: Float = if (flipX) region.getMaxU else region.getMinU
    val maxU: Float = if (flipX) region.getMinU else region.getMaxU
    val minV: Float = if (flipY) region.getMaxV else region.getMinV
    val maxV: Float = if (flipY) region.getMinV else region.getMaxV
    batch.addVertex(bottomLeft.x + x, bottomLeft.y + y, zIndex, minU, maxV, color)
    batch.addVertex(topLeft.x + x, topLeft.y + y, zIndex, minU, minV, color)
    batch.addVertex(topRight.x + x, topRight.y + y, zIndex, maxU, minV, color)
    batch.addVertex(bottomRight.x + x, bottomRight.y + y, zIndex, maxU, maxV, color)
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