package org.jglr.phiengine.network.packets.client

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.channels.NetworkChannel
import org.jglr.phiengine.network.client.ClientNetHandler
import org.jglr.phiengine.network.packets.server.PacketStatus
import org.jglr.phiengine.network.server.ServerNetHandler
import org.jglr.phiengine.network.{NetworkHandler, PacketHandler, Packet}
import org.jglr.phiengine.network.packets.EmptyPacket

class PacketAskStatus(id: Int) extends EmptyPacket(id)

object PacketAskStatusHandler extends PacketHandler[PacketAskStatus] {
  override def getPacketClass: Class[PacketAskStatus] = classOf[PacketAskStatus]

  override def handleClient(packet: PacketAskStatus, channel: NetworkChannel, netHandler: ClientNetHandler): Unit = {

  }

  override def handleServer(packet: PacketAskStatus, channel: NetworkChannel, netHandler: ServerNetHandler): Unit = {
    channel.answerFlushTo(packet, new PacketStatus(netHandler.backing.createStatus, 0))
  }
}
