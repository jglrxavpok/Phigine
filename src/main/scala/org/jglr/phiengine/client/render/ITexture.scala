package org.jglr.phiengine.client.render

trait ITexture {
  def bind(slot: Int = 0): Unit

  def unbind(): Unit

  def getWidth(): Int

  def getHeight(): Int
}
