package org.jglr.phiengine.network.packets

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.Packet

class EmptyPacket(id: Int) extends Packet(id) {
  override def read(buffer: ByteBuf): Unit = {}

  override def write(buffer: ByteBuf): Unit = {}
}

