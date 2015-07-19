package org.jglr.phiengine.client.text

import java.awt.image.BufferedImage
import java.awt.{Font => JFont, RenderingHints, Color}
import java.io.{InputStream, File}

import org.jglr.phiengine.core.io.FilePointer

object FontFormat extends Enumeration {
  sealed abstract class EnumVal(val rawValue: Int)

  case object PLAIN extends EnumVal(JFont.PLAIN)
  case object BOLD extends EnumVal(JFont.BOLD)
  case object ITALIC extends EnumVal(JFont.ITALIC)

  type Type = EnumVal
}

object Font {
  def get(name: String, size: Int, formats: FontFormat.Type*): Font = {
    new Font(new JFont(name, getFormat(formats), size))
  }

  def create(input: InputStream, formats: FontFormat.Type*): Font = {
    val javaFont = JFont.createFont(getFormat(formats), input)
    new Font(javaFont)
  }

  def create(input: File, formats: FontFormat.Type*): Font = {
    val javaFont = JFont.createFont(getFormat(formats), input)
    new Font(javaFont)
  }

  def create(input: FilePointer, formats: FontFormat.Type*): Font = {
    val javaFont = JFont.createFont(getFormat(formats), input.createInputStream)
    new Font(javaFont)
  }

  def getFormat(formats: Seq[FontFormat.Type]): Int = {
    var formatVal = JFont.PLAIN
    if(formats.nonEmpty) {
      formats.foreach((f: FontFormat.Type) => {
        formatVal += f.rawValue
      })
    }
    formatVal
  }
}

class Font(javaFont: JFont) {

  val tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
  val graph = tmpImg.createGraphics()
  graph.setFont(javaFont)
  val metrics = graph.getFontMetrics
  val height = metrics.getHeight

  def generateImage(character: Char): BufferedImage = {
    val w = metrics.charWidth(character)
    val h = metrics.getHeight
    if(w <= 0 || h <= 0)
      return null
    val result = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB)
    val g = result.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setFont(javaFont)
    g.setColor(Color.white)
    g.drawString(String.valueOf(character), 0, metrics.getAscent)
    g.dispose()
    result
  }

  def getName(): String = {
    javaFont.getFontName
  }

  def getWidth(c: Char): Float = {
    metrics.charWidth(c)
  }

  def getHeight(c: Char): Float = {
    height
  }

  def getWidth(text: String): Float = {
    val chars = text.toCharArray
    var size = 0f
    for(c <- chars) {
      size += getWidth(c)
    }
    size
  }

  def getHeight(text: String): Float = {
    val chars = text.toCharArray
    var size = 0f
    for(c <- chars) {
      val h = getHeight(c)
      if(h > size)
        size = h
    }
    size
  }
}
