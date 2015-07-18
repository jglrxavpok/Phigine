package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.Spritesheet
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.{AutoUpdateable, ITickable}

class Animation(val spritesheet: Spritesheet, val minIndex: Int, val maxIndex: Int, val spritesPerSecond: Float) extends AutoUpdateable {
  private var offset: Float = 0
  private var currentIndex: Int = 0

  currentIndex = minIndex

  def this(spritesheet: Spritesheet, spritesPerSecond: Float) {
    this(spritesheet, 0, spritesheet.getSpriteCount, spritesPerSecond)
  }

  def tick(delta: Float) {
    offset += spritesPerSecond * delta
    currentIndex = (Math.floor(offset).toInt % (maxIndex - minIndex)) + minIndex
  }

  def draw(batch: SpriteBatch, x: Float, y: Float) {
    val s: Sprite = getCurrent
    s.setPosition(x, y)
    s.draw(batch)
  }

  def getCurrent: Sprite = {
    spritesheet.getSprites(currentIndex)
  }

  def getSpritesPerSecond: Float = {
    spritesPerSecond
  }

  def getSpritesheet: Spritesheet = {
    spritesheet
  }
}