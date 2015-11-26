package org.jglr.phiengine.network.packets.server

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.Packet
import org.jglr.phiengine.network.utils.ServerStatus
import org.jglr.phiengine.network.utils.NettyHelper._

class PacketStatus(id: Int, val serverInfos: ServerStatus) extends Packet(id) {

  def this(id: Int) {
    this(id, new ServerStatus)
  }

  override def read(buffer: ByteBuf): Unit = {
    serverInfos.name = readUTF8(buffer)
    serverInfos.playerCount = buffer.readInt()
    serverInfos.maxPlayerCount = buffer.readInt()
  }

  override def write(buffer: ByteBuf): Unit = {
    writeUTF8(serverInfos.name, buffer)
    buffer.writeInt(serverInfos.playerCount)
    buffer.writeInt(serverInfos.maxPlayerCount)
  }
}
