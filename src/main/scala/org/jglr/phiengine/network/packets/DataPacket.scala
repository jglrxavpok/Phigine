package org.jglr.phiengine.network.packets

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.Packet
import org.jglr.phiengine.network.utils.NetworkSerializable

abstract class DataPacket[T <: NetworkSerializable](data: T, dataClass: Class[T], id: Int) extends Packet(id) {

  override def read(buffer: ByteBuf): Unit = {
    data.read(buffer)
  }

  override def write(buffer: ByteBuf): Unit = {
    data.write(buffer)
  }
}
