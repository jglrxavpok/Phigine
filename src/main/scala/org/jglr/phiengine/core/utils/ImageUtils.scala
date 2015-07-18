package org.jglr.phiengine.core.utils

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, IOException}
import java.util.{Map, HashMap}
import javax.imageio.ImageIO

import org.jglr.phiengine.core.io.FilePointer

object ImageUtils {
  private var offset: Int = 0
  private val imgs: Map[String, BufferedImage] = new HashMap[String, BufferedImage]
  private val imagesMap: Map[FilePointer, BufferedImage] = new HashMap[FilePointer, BufferedImage]

  private def btoi(b: Byte): Int = {
    val a: Int = b
    if (a < 0) 256 + a else a
  }

  private def read(buf: Array[Byte]): Int = {
    val result = btoi(buf(offset))
    offset+=1
    result
  }

  /**
   * Creates a BufferedImage from given TGA data
   */
  @throws(classOf[IOException])
  def decodeTGA(buf: Array[Byte]): BufferedImage = {
    offset = 0
    for(i <- 0 until 12) {
      read(buf)
    }
    val width: Int = read(buf) + (read(buf) << 8)
    val height: Int = read(buf) + (read(buf) << 8)
    read(buf)
    read(buf)
    var n: Int = width * height
    val pixels: Array[Int] = new Array[Int](n)
    var idx: Int = 0
    if (buf(2) == 0x02 && buf(16) == 0x20) {
      while (n > 0) {
        val b: Int = read(buf)
        val g: Int = read(buf)
        val r: Int = read(buf)
        val a: Int = read(buf)
        val v: Int = (a << 24) | (r << 16) | (g << 8) | b
        pixels(idx) = v
        idx+=1
        n -= 1
      }
    }
    else if (buf(2) == 0x02 && buf(16) == 0x18) {
      while (n > 0) {
        val b: Int = read(buf)
        val g: Int = read(buf)
        val r: Int = read(buf)
        val a: Int = 255
        val v: Int = (a << 24) | (r << 16) | (g << 8) | b
        pixels(idx) = v
        idx+=1
        n -= 1
      }
    }
    else {
      while (n > 0) {
        var nb: Int = read(buf)
        if ((nb & 0x80) == 0) {
          for(i <- 0 to nb) {
            val b: Int = read(buf)
            val g: Int = read(buf)
            val r: Int = read(buf)
            pixels(idx) = 0xff000000 | (r << 16) | (g << 8) | b
            idx+=1
          }
        } else {
          nb &= 0x7f
          val b: Int = read(buf)
          val g: Int = read(buf)
          val r: Int = read(buf)
          val v: Int = 0xff000000 | (r << 16) | (g << 8) | b
          for(i <- 0 to nb) {
            pixels(idx) = v
            idx+=1
          }
        }
        n -= nb + 1
      }
    }
    val bimg: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    bimg.setRGB(0, 0, width, height, pixels, 0, width)
    return bimg
  }

  @throws(classOf[IOException])
  def loadImage(res: FilePointer): BufferedImage = {
    if (imagesMap.containsKey(res)) return imagesMap.get(res)
    var img: BufferedImage = null
    if (res.getExtension.equalsIgnoreCase("tga")) {
      img = ImageUtils.decodeTGA(res.readAll)
    }
    else img = ImageIO.read(new ByteArrayInputStream(res.readAll))
    imagesMap.put(res, img)
    return img
  }

  def resize(img: BufferedImage, w: Int, h: Int): BufferedImage = {
    val resized: BufferedImage = new BufferedImage(w, h, img.getType)
    val g: Graphics = resized.createGraphics
    g.drawImage(img, 0, 0, w, h, null)
    g.dispose
    resized
  }
}
