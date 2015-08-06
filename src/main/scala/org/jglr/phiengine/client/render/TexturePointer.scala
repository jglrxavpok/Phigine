package org.jglr.phiengine.client.render

import java.util

import com.google.common.collect.Maps
import org.jglr.phiengine.core.io.{FileType, FilePointer}
import org.jglr.phiengine.core.utils.IDisposable
import java.util.Map

object TexturePointer {
  private val cache: Map[FilePointer, Texture] = new util.HashMap[FilePointer, Texture]
}

class TexturePointer(file: FilePointer) extends IDisposable {
  private final var texture: Texture = null

  def this(classpath: String) {
    this(new FilePointer(classpath, FileType.CLASSPATH))
  }

  if (TexturePointer.cache.containsKey(file)) {
    texture = TexturePointer.cache.get(file)
  }
  else {
    texture = new Texture(file)
    TexturePointer.cache.put(file, texture)
  }
  texture.incRefCount()

  def bind() {
    bind(0)
  }

  def bind(slot: Int) {
    texture.bind(slot)
  }

  def unbind() {
    texture.unbind()
  }

  def getHeight: Int = {
    texture.getHeight
  }

  def getWidth: Int = {
    texture.getWidth
  }

  def dispose {
    texture.decRefCount()
    texture.dispose()
  }
}