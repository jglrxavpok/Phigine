package org.jglr.phiengine.network.utils

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.core.utils.Version
import org.jglr.phiengine.network.utils.NettyHelper._

class ServerStatus extends NetworkSerializable {

  var name: String = null
  var playerCount: Int = -1
  var maxPlayerCount: Int = -1
  var motd: String = null
  var networkingVersion: Version = new Version()

  override def read(buffer: ByteBuf): Unit = {
    name = readUTF8(buffer)
    motd = readUTF8(buffer)
    playerCount = buffer.readInt()
    maxPlayerCount = buffer.readInt()
    networkingVersion.read(buffer)
  }

  override def write(buffer: ByteBuf): Unit = {
    writeUTF8(name, buffer)
    writeUTF8(motd, buffer)
    buffer.writeInt(playerCount)
    buffer.writeInt(maxPlayerCount)
    networkingVersion.write(buffer)
  }
}
