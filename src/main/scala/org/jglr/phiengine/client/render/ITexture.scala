package org.jglr.phiengine.client.render

/**
 * Represents an object that behaves like a [[org.jglr.phiengine.client.render.Texture Texture]].
 * Often used when given object uses a texture internally but doesn't necessary want to expose it.
 */
trait ITexture {
  def bind(slot: Int = 0): Unit

  def unbind(): Unit

  def getWidth: Int

  def getHeight: Int
}
