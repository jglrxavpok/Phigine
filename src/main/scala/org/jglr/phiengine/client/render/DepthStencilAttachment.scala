package org.jglr.phiengine.client.render

import java.nio.ByteBuffer

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._

class DepthStencilAttachment(w: Int, h: Int) extends FramebufferAttachment(GL_DEPTH_STENCIL_ATTACHMENT) {

  val depthTextureId = glGenTextures()
  glBindTexture(GL_TEXTURE_2D, depthTextureId)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

  glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, w, h, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, null.asInstanceOf[ByteBuffer])
  glBindTexture(GL_TEXTURE_2D, 0)

  override def getTarget: Int = GL_TEXTURE_2D

  override def getTextureID: Int = depthTextureId

  override def isDrawBuffer: Boolean = false
}
