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

object Framebuffer {
  def createFromResolution(w: Int, h: Int): Texture = {
    new Texture(new FilePointer(s"resolution${w}x$h "+Math.random(), FileType.VIRTUAL), GL_NEAREST, new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
  }

  def createUsualFramebufferBuffer(w: Int, h: Int): Framebuffer = new Framebuffer(w, h, null: Texture)
}

class Framebuffer(val w: Int, val h: Int, val attachments: FramebufferAttachment*) {

  private var previous: Int = 0

  def this(w: Int, h: Int, _colorTexture: Texture = null) = {
    this(w, h, new TextureAttachment(0,_colorTexture), new DepthStencilAttachment(w, h))
  }

  val id = glGenFramebuffers()
  bind()

  val drawBuffers = Array.newBuilder[Int]
  var drawIndex = 0
  for(attach <- attachments) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, attach.getTypeID, attach.getTarget, attach.getTextureID, attach.getLevel)
    if(attach.isDrawBuffer) {
      drawBuffers += attach.getTypeID
    }
  }
  glDrawBuffers(Buffers.wrapInt(drawBuffers.result()))

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

  unbind()

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
