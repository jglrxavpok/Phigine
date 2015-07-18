package org.jglr.phiengine.core

import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

object Buffers {
  def wrap(bytes: Array[Byte]): ByteBuffer = {
    val buffer: ByteBuffer = BufferUtils.createByteBuffer(bytes.length)
    buffer.put(bytes)
    buffer.flip
    buffer
  }
}