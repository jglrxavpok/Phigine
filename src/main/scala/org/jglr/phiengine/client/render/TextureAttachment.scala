package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.ui
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._

class TextureAttachment(val attachID: Int = 0, _texture: Texture = null) extends FramebufferAttachment(GL_COLOR_ATTACHMENT0 + attachID) {
  val texture = if(_texture == null) {
    Framebuffer.createFromResolution(ui.width, ui.height)
  } else {
    _texture
  }

  override def getTarget: Int = GL_TEXTURE_2D

  override def getTextureID: Int = texture.handle.texID

  override def isDrawBuffer: Boolean = true
}
