package org.jglr.phiengine.network.utils

import io.netty.buffer.ByteBuf

object NetworkSerializable {

  def create[T <: NetworkSerializable](serializableClass: Class[T]): T = {
    serializableClass.getConstructor().newInstance()
  }
}

trait NetworkSerializable {

  def write(buf: ByteBuf): Unit

  def read(buf: ByteBuf): Unit
}
