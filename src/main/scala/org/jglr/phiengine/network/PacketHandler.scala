package org.jglr.phiengine.network

import org.jglr.phiengine.network.channels.NetworkChannel
import org.jglr.phiengine.network.client.ClientNetHandler
import org.jglr.phiengine.network.server.ServerNetHandler

trait PacketHandler[T <: Packet] {
  def getPacketClass: Class[T]

  def handleClient(packet: T, channel: NetworkChannel, netHandler: ClientNetHandler)

  def handleServer(packet: T, channel: NetworkChannel, netHandler: ServerNetHandler)
}