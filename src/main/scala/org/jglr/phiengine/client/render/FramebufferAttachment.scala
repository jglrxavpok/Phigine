package org.jglr.phiengine.client.render

abstract class FramebufferAttachment(id: Int) {

  def getTarget: Int

  def getTextureID: Int

  def getLevel: Int = 0

  def isDrawBuffer: Boolean

  final def getTypeID: Int = id
}
