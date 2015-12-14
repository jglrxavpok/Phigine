package org.jglr.phiengine.client.text

import java.awt.image.BufferedImage

import org.jglr.phiengine.client.render.{Color, Colors, TextureMap}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.core.io.{FileType, FilePointer}
import org.jglr.phiengine.core.utils.ImageUtils

object FontRenderer {
  def unicodeChars(range: Range, added: Char*): Array[Char] = {
    val buffer = new StringBuilder
    for(c <- range) {
      buffer.append(c.toChar)
    }
    for(c <- added) {
      buffer.append(c)
    }
    buffer.toString().toCharArray
  }

  val ASCII = unicodeChars(0 to 256)

  val missingImage = ImageUtils.loadImage("assets/fonts/missing.png")

  val NUMBERS: Array[Char] = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '%', ',', ':')
}

class FontRenderer(val supportedChars: Array[Char], _font: Font = null) {
  val batch = new SpriteBatch()

  val font =
    if(_font == null) {
      Font.get("Arial", 28, false)
    }
    else {
      _font
    }

  val map = new TextureMap(
    new FilePointer("fonts/"+font.getName+"/"+supportedChars.hashCode()+"_"+_font.javaFont.getFontName+"/", FileType.VIRTUAL))

  for(c <- supportedChars) {
    val img = font.generateImage(c)
    if(img != null)
      map.generateIcon(img)
    else
      map.generateIcon(""+c.toInt)
  }
  val missing = map.generateIcon(FontRenderer.missingImage)
  map.compile
  map.writeDebugTexture()

  def drawChar(c: Char, posX: Float, posY: Float, z: Float, batch: SpriteBatch, color: Color, scale: Float = 1f): Boolean = {
    val index = indexOf(c)
    val region =
      if(index >= 0) {
        map.get(index)
      } else {
        missing
      }
    val w = map.getWidth*(region.getMaxU-region.getMinU)
    val h = map.getHeight*(region.getMaxV-region.getMinV)
    batch.draw(region, posX, posY-h, z, w, h, color)
    true
  }

  def renderString(string: String, x: Float, y: Float, z: Float, color: Color, scale: Float = 1f, batch: SpriteBatch = batch) = {
    val wasDrawing = batch.isDrawing
    if(!wasDrawing)
      batch.begin()
    batch.setTexture(map)
    val chars = string.toCharArray
    var posX = x
    var posY = y
    for(c <- chars) {
      if(drawChar(c, posX, posY, z, batch, color, scale))
        posX += font.getWidth(c)*scale
    }
   if(!wasDrawing)
      batch.end()
  }

  def indexOf(char: Char): Int = {
    for(i <- supportedChars.indices) {
      if(Character.compare(char, supportedChars(i)) == 0)
        return i
    }
    -1
  }

  def getWidth(text: String): Float = {
    font.getWidth(text)
  }

  def getHeight(text: String): Float = {
    font.getHeight(text)
  }
}
