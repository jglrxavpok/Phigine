package org.jglr.phiengine.client.render

import java.awt.image.BufferedImage
import java.nio.ByteBuffer

import org.jglr.phiengine.client.render.Texture
import org.jglr.phiengine.client.ui
import org.jglr.phiengine.core.io.{FileType, FilePointer}
import org.jglr.phiengine.core.utils.Buffers
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

private object Framebuffer {
  def createFromResolution(w: Int, h: Int): Texture = {
    new Texture(new FilePointer(s"resolution${w}x$h "+Math.random(), FileType.VIRTUAL), GL_NEAREST, new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
  }
}

class Framebuffer(val w: Int = ui.width, val h: Int = ui.height, _colorTexture: Texture = null) {

  private var previous: Int = 0

  val colorTexture = if(_colorTexture == null) {
    Framebuffer.createFromResolution(w, h)
  } else {
    _colorTexture
  }

  val depthTextureId = glGenTextures()
  glBindTexture(GL_TEXTURE_2D, depthTextureId)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

  glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, w, h, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null.asInstanceOf[ByteBuffer])
  glBindTexture(GL_TEXTURE_2D, 0)

  val id = glGenFramebuffers()
  glBindFramebuffer(GL_FRAMEBUFFER, id)
  glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture.handle.texID, 0)
  glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0)

  val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
  if (status != GL_FRAMEBUFFER_COMPLETE) {
    println("Framebuffer creation failed ")
    status match {
      case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT =>
        println("Incomplete attachment")
      case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT =>
        println("No attachments")
      case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER =>
        println("Incomplete draw buffer")
      case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER =>
        println("Incomplete read buffer")
      case GL_FRAMEBUFFER_UNSUPPORTED =>
        println("Unsupported format")
      case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE =>
        println("Invalid multisamples")
      case _ =>
        println("Reason unknown")
    }
  }

  glBindFramebuffer(GL_FRAMEBUFFER, 0)

  def bind(): Unit = {
    previous = glGetInteger(GL_FRAMEBUFFER_BINDING)
    glBindFramebuffer(GL_FRAMEBUFFER, id)
    glViewport(0,0,w,h)
  }

  def unbind(): Unit = {
    glBindFramebuffer(GL_FRAMEBUFFER, previous)
    glViewport(0,0,ui.width,ui.height)
  }

  def dispose(): Unit = {
    glDeleteFramebuffers(id)
  }

  def copyFrom(other: Framebuffer): Unit = {
    other.copyTo(id, w, h)
  }

  def copyFrom(otherID: Int, otherW: Int, otherH: Int): Unit = {
    glBindFramebuffer(GL_READ_FRAMEBUFFER, otherID)
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id)
    glBlitFramebuffer(0, 0, otherW, otherH, 0, 0, w, h, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST)
    glBindFramebuffer(GL_READ_FRAMEBUFFER, 0)
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
  }

  def copyFromWindow(): Unit = {
    copyFrom(0, ui.width, ui.height)
  }

  def copyTo(other: Framebuffer): Unit = {
    copyTo(other.id, other.w, other.h)
  }

  def copyTo(otherID: Int, otherW: Int, otherH: Int): Unit = {
    glBindFramebuffer(GL_READ_FRAMEBUFFER, id)
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, otherID)
    glBlitFramebuffer(0, 0, w, h, 0, 0, otherW, otherH, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST)
    glBindFramebuffer(GL_READ_FRAMEBUFFER, 0)
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
  }

  def copyToWindow(): Unit = {
    copyTo(0, ui.width, ui.height)
  }
}
