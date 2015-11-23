package org.jglr.phiengine.client.render.deferred

import org.jglr.phiengine.client.render.{DepthStencilAttachment, TextureAttachment, Framebuffer}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.Buffers
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import GBufferAttachs._

object GBufferAttachs {
  val Diffuse = 0
  val TextureCoordinates = 1
  val WorldPosition = 2
  val Normal = 3
  val DepthStencil = 4
  val Result = 5
}

class GBuffer(w: Int, h: Int) extends Framebuffer(w, h,
  new TextureAttachment(0), new TextureAttachment(1), new TextureAttachment(2), new TextureAttachment(3),
  new DepthStencilAttachment(w, h) /*new TextureAttachment(4)*/) {

  val diffuseID = attachments(Diffuse).getTextureID
  val texcoordsID = attachments(TextureCoordinates).getTextureID
  val positionID = attachments(WorldPosition).getTextureID
  val normalsID = attachments(Normal).getTextureID
  //val result = attachments(Result).getTextureID
  val depthStencilID = attachments(DepthStencil).getTextureID

  override def bindReading(): Unit = {
    super.bindReading()
    for(i <- Diffuse to Normal) {
      if(attachments(i).getTypeID != GL_DEPTH_STENCIL_ATTACHMENT) {
        glActiveTexture(GL_TEXTURE0+i)
        glBindTexture(GL_TEXTURE_2D, attachments(i).getTextureID)
      }
    }
  }

  def startFrame(): Unit = {
    bindWriting()
    glDrawBuffer(GL_COLOR_ATTACHMENT4) // result attachment
    glClear(GL_COLOR_BUFFER_BIT)
  }

  val geomBuffers = Buffers.wrapInt(Array(GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2))

  def bindGeometryPass(): Unit = {
    bindWriting()
    glDrawBuffers(geomBuffers)
  }

  def bindStencilPass(): Unit = {
    glDrawBuffer(GL_NONE)
  }

  def bindLightPass(): Unit = {
    glDrawBuffer(GL_COLOR_ATTACHMENT4);
    for (i <- Diffuse to Normal) {
      glActiveTexture(GL_TEXTURE0 + i);
      glBindTexture(GL_TEXTURE_2D, attachments(i).getTextureID);
    }
  }

  def bindFinalPass(): Unit = {
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
    glReadBuffer(GL_COLOR_ATTACHMENT4);
  }
}
