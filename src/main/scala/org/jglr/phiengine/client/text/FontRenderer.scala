package org.jglr.phiengine.client.text

import org.jglr.phiengine.client.render.{Color, Colors, TextureMap}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.core.io.{FileType, FilePointer}

object FontRenderer {
  def unicodeChars(range: Range): Array[Char] = {
    val buffer = new StringBuilder
    for(c <- range) {
      buffer.append(c.toChar)
    }
    buffer.toString().toCharArray
  }

  val ASCII = unicodeChars(0 to 256)
}

class FontRenderer(val supportedChars: Array[Char], _font: Font = null) {
  val batch = new SpriteBatch()

  val font =
    if(_font == null) {
      Font.get("Arial", 28)
    }
    else {
      _font
    }

  val map = new TextureMap(new FilePointer("fonts/"+font.getName()+"/", FileType.VIRTUAL))

  for(c <- supportedChars) {
    val img = font.generateImage(c)
    if(img != null)
      map.generateIcon(img)
    else
      map.generateIcon(""+c.toInt)
  }
  map.compile

  def drawChar(c: Char, posX: Float, posY: Float, z: Float, batch: SpriteBatch, color: Color, scale: Float = 1f): Boolean = {
    val index = indexOf(c)
    if(index >= 0) {
      val region = map.get(index)
      val w = map.getWidth()*(region.getMaxU-region.getMinU)
      val h = map.getHeight()*(region.getMaxV-region.getMinV)
      batch.addVertex(posX, posY, z, region.getMinU, 1f-region.getMaxV, color)
      batch.addVertex(posX, posY+h, z, region.getMinU, 1f-region.getMinV, color)
      batch.addVertex(posX+w, posY+h, z, region.getMaxU, 1f-region.getMinV, color)
      batch.addVertex(posX+w, posY, z, region.getMaxU, 1f-region.getMaxV, color)

      batch.addIndex(1)
      batch.addIndex(0)
      batch.addIndex(2)

      batch.addIndex(2)
      batch.addIndex(0)
      batch.addIndex(3)

      batch.nextSprite()
      true
    } else {
      false
    }
  }

  def renderString(string: String, x: Float, y: Float, z: Float, color: Color, scale: Float = 1f) = {
    batch.begin()
    batch.setTexture(map)
    val chars = string.toCharArray
    var posX = x
    var posY = y
    for(c <- chars) {
      if(drawChar(c, posX, posY, z, batch, color, scale))
        posX += font.getWidth(c)*scale
    }
    batch.end()
  }

  def indexOf(char: Char): Int = {
    for(i <- supportedChars.indices) {
      if(supportedChars(i) == char)
        return i
    }
    -1
  }
}
