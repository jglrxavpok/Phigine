package org.jglr.phiengine.network.packets.server

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.channels.NetworkChannel
import org.jglr.phiengine.network.client.ClientNetHandler
import org.jglr.phiengine.network.packets.DataPacket
import org.jglr.phiengine.network.server.ServerNetHandler
import org.jglr.phiengine.network.{NetworkHandler, PacketHandler, Packet}
import org.jglr.phiengine.network.utils.ServerStatus
import org.jglr.phiengine.network.utils.NettyHelper._

class PacketStatus(val serverInfos: ServerStatus, id: Int) extends DataPacket(serverInfos, classOf[ServerStatus], id) {

  def this(id: Int) {
    this(new ServerStatus, id)
  }

}

object PacketStatusHandler extends PacketHandler[PacketStatus] {
  override def getPacketClass: Class[PacketStatus] = classOf[PacketStatus]

  override def handleClient(packet: PacketStatus, channel: NetworkChannel, netHandler: ClientNetHandler): Unit = {
    println("received status: Server name = "+packet.serverInfos.name+", Player count = "+packet.serverInfos.playerCount+"/"+packet.serverInfos.maxPlayerCount)
    println("received status: Server motd = "+packet.serverInfos.motd+", Networking version = "+packet.serverInfos.networkingVersion)
  }

  override def handleServer(packet: PacketStatus, channel: NetworkChannel, netHandler: ServerNetHandler): Unit = {
  }
}
