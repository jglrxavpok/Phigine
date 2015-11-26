package org.jglr.phiengine.network.utils

import io.netty.buffer.ByteBuf

object NettyHelper {

  def writeUTF8(string: String, buffer: ByteBuf): Unit = {
    val bytes = string.getBytes()
    buffer.writeInt(bytes.length)
    buffer.writeBytes(bytes)
  }

  def readUTF8(buffer: ByteBuf): String = {
    val length = buffer.readInt()
    val bytes = new Array[Byte](length)
    buffer.readBytes(bytes)
    new String(bytes, "UTF-8")
  }

}
