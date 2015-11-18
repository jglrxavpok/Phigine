package org.jglr.phiengine.client.render

import java.util.HashMap

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.{FileType, FilePointer}
import org.jglr.phiengine.core.utils.{ReferenceCounter, IDisposable}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.{GL11, GL12}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.ByteBuffer
import org.lwjgl.opengl.ARBBindlessTexture._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._

object Textures {
  
  var shouldCache = false
  var outputList = false

  var cache = new HashMap[FilePointer, TextureHandle]
  
  def createFrom(pointerLoc: String, img: BufferedImage): Texture = {
    new Texture(new FilePointer(pointerLoc, FileType.VIRTUAL), GL11.GL_NEAREST, img)
  }
}

class Texture(pointer: FilePointer, filter: Int = GL_NEAREST, _img: BufferedImage = null) extends IDisposable with ITexture {
  val handle: TextureHandle =
    if(Textures.shouldCache) {
      if(Textures.cache.containsKey(pointer)) {
        Textures.cache.get(pointer)
      } else {
        val newHandle = new TextureHandle(pointer, filter, _img)
        Textures.cache.put(pointer, newHandle)
        newHandle
      }
    } else {
      new TextureHandle(pointer, filter, _img)
    }

  def getWidth: Int = {
    handle.getWidth
  }

  def getHeight: Int = {
    handle.getHeight
  }

  def bind(slot: Int = 0): Unit = {
    handle.bind(slot)
  }

  def unbind(): Unit = {
    handle.unbind
  }

  def dispose() {
    handle.dispose()
  }

  def getFilePointer: FilePointer = {
    handle.getFilePointer
  }
}

class TextureHandle(pointer: FilePointer, filter: Int, _img: BufferedImage = null) extends ReferenceCounter with IDisposable with ITexture {
  var img: BufferedImage =
    if(_img == null) {
      try {
        ImageIO.read(pointer.createInputStream)
      }
      catch {
        case e: Exception =>
          e.printStackTrace()
          PhiEngine.getInstance.getLogger.error("Could not find texture " + pointer + ", loading placeholder texture instead!")
          val placeholder = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB)
          placeholder.setRGB(0,0,0xFFFFFFFF)
          placeholder.setRGB(1,1,0xFFFFFFFF)
          placeholder.setRGB(0,1,0xFF000000)
          placeholder.setRGB(1,0,0xFF000000)
          placeholder
      }
    }
    else {
      _img
    }

  private val w = img.getWidth
  private val h = img.getHeight
  val pixels: Array[Int] = img.getRGB(0, 0, w, h, null, 0, w)
  val buffer: ByteBuffer = BufferUtils.createByteBuffer(w * h * 4)
  var y: Int = h-1
  while(y >= 0) {
    for(x <- 0 until w) {
      val color: Int = pixels(x + y * w)
      val a: Int = color >> 24 & 0xFF
      val r: Int = color >> 16 & 0xFF
      val g: Int = color >> 8 & 0xFF
      val b: Int = color & 0xFF
      buffer.put(r.toByte)
      buffer.put(g.toByte)
      buffer.put(b.toByte)
      buffer.put(a.toByte)
    }
    y -= 1;
  }
  buffer.flip
  val texID = glGenTextures
  bind()
  glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0)
  glTexParameteri(GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter)
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
  unbind()

  def getWidth: Int = {
    w
  }

  def getHeight: Int = {
    h
  }

  def bind(slot: Int = 0): Unit = {
    glActiveTexture(GL_TEXTURE0 + slot)
    glBindTexture(GL_TEXTURE_2D, texID)
  }

  def unbind(): Unit = {
    glBindTexture(GL_TEXTURE_2D, 0)
  }

  def dispose() {
    if (isDisposable) glDeleteTextures(texID)
  }

  def getFilePointer: FilePointer = {
    pointer
  }
}