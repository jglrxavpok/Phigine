package org.jglr.phiengine.client.render.deferred

import org.jglr.phiengine.client.render.{DepthStencilAttachment, TextureAttachment, Framebuffer}

object GBufferAttachs {
  val Diffuse = 0
  val TextureCoordinates = 1
  val WorldPosition = 2
  val Normal = 3
  val DepthStencil = 4
}

class GBuffer(w: Int, h: Int) extends Framebuffer(w, h,
  new TextureAttachment(0), new TextureAttachment(1), new TextureAttachment(2), new TextureAttachment(3),
  new DepthStencilAttachment(w, h)) {

  val diffuseID = attachments(GBufferAttachs.Diffuse).getTextureID
  val texcoordsID = attachments(GBufferAttachs.TextureCoordinates).getTextureID
  val positionID = attachments(GBufferAttachs.WorldPosition).getTextureID
  val normalsID = attachments(GBufferAttachs.Normal).getTextureID
  val depthStencilID = attachments(GBufferAttachs.DepthStencil).getTextureID

}
