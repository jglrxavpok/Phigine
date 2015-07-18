package org.jglr.phiengine.client.render

import java.awt._
import java.awt.image._
import java.util.{List, ArrayList}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.maths.{Vec2, MathHelper}
import org.jglr.phiengine.core.utils.ImageUtils

import scala.collection.JavaConversions._

class Stitcher {
  private var imgs: List[BufferedImage] = null
  private var slots: List[Slot] = null
  private var tileWidth: Int = 0
  private var tileHeight: Int = 0
  private var emptySlotImage: BufferedImage = null
  private var putInCorner: Boolean = false

  def this(emptyImage: BufferedImage, putInCorner: Boolean) {
    this()
    this.emptySlotImage = emptyImage
    slots = new ArrayList[Slot]
    imgs = new ArrayList[BufferedImage]
    this.putInCorner = putInCorner
    tileWidth = -1
    tileHeight = -1
  }

  /**
   * Adds a image to the list
   */
  def addImage(img: BufferedImage, name: String): Int = {
    return addImage(img, name, false)
  }

  /**
   * Adds a image to the list and resizes it if asked
   */
  def addImage(_img: BufferedImage, name: String, forceResize: Boolean): Int = {
    var img = _img
    if(putInCorner && !forceResize) {
      tileWidth = Math.max(tileWidth, _img.getWidth)
      tileHeight = Math.max(tileHeight, _img.getHeight)
    } else if (tileWidth == -1 || tileHeight == -1) {
      tileWidth = img.getWidth
      tileHeight = img.getHeight
    } else if (img.getWidth != tileWidth || img.getHeight != tileHeight) {
      if (!forceResize && !putInCorner) {
        PhiEngine.getInstance().getLogger().error("Unexpected size in " + name + ": " + img.getWidth + "x" + img.getHeight + "px, expected " + tileWidth + "x" + tileHeight + "px. Image index: " + imgs.size)
      }
      else if (forceResize) {
        img = ImageUtils.resize(img, tileWidth, tileHeight)
      }
    }
    imgs.add(img)
    return imgs.size - 1
  }

  /**
   * Creates a big BufferImage containing all previously given images
   */
  def stitch: BufferedImage = {
    var nbrY: Int = MathHelper.upperPowerOf2(Math.floor(Math.sqrt(imgs.size)).toInt)
    val nbrX: Int = Math.ceil(imgs.size.toDouble / nbrY.toDouble).toInt
    while ((nbrX * nbrY - (imgs.size - 1)) > nbrY) {
      nbrY -= 1
    }
    var width: Int = nbrX * tileWidth
    var height: Int = nbrY * tileHeight
    if (height < tileHeight) height = tileHeight
    if (width < tileWidth) width = tileWidth
    val result: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics = result.createGraphics
    for(i <- imgs.indices) {
      val column: Int = i % nbrX
      val row: Int = i / nbrX
      val x: Int = column * tileWidth
      val y: Int = row * tileHeight
      val img = imgs.get(i)
      g.drawImage(img, x, y, null)
      val minUV = getCoords(x, y, width, height)
      val maxUV = getCoords(x+img.getWidth, y+img.getHeight, width, height)
      slots.add(new Slot(minUV.x, minUV.y, maxUV.x, maxUV.y, width, height))
    }
    emptySlotImage = ImageUtils.resize(emptySlotImage, tileWidth, tileHeight)
    for (n <- imgs.size until nbrX * nbrY) {
      val column: Int = n % nbrX
      val row: Int = n / nbrX
      g.drawImage(emptySlotImage, column * tileWidth, row * tileHeight, null)
    }
    g.dispose()
    result
  }

  private def getCoords(x: Int, y: Int, width: Int, height: Int): Vec2 = {
    val xpos: Float = x + 0.5f
    val ypos: Float = y + 0.5f
    new Vec2(xpos / width.toFloat, ypos / height.toFloat)
  }

  /**
   * Gets min U coordinate for given index
   */
  def getMinU(index: Int): Float = {
    return slots.get(index).minU
  }

  /**
   * Gets min V coordinate for given index
   */
  def getMinV(index: Int): Float = {
    return slots.get(index).minV
  }

  /**
   * Gets max U coordinate for given index
   */
  def getMaxU(index: Int): Float = {
    return slots.get(index).maxU
  }

  /**
   * Gets max V coordinate for given index
   */
  def getMaxV(index: Int): Float = {
    return slots.get(index).maxV
  }

  /**
   * Gets width for given index
   */
  def getWidth(index: Int): Int = {
    return slots.get(index).width
  }

  /**
   * Gets height for given index
   */
  def getHeight(index: Int): Int = {
    return slots.get(index).height
  }

  private class Slot(var minU: Float, var minV: Float, var maxU: Float, var maxV: Float, var width: Int, var height: Int)

  /**
   * Sets tile width
   */
  def setTileWidth(w: Int) {
    tileWidth = w
  }

  /**
   * Sets tile height
   */
  def setTileHeight(h: Int) {
    tileHeight = h
  }

  /**
   * Gets tile width
   */
  def getTileWidth: Int = {
    return tileWidth
  }

  /**
   * Gets tile height
   */
  def getTileHeight: Int = {
    return tileHeight
  }
}