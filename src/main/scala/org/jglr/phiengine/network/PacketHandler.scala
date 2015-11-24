package org.jglr.phiengine.network

trait PacketHandler[T <: Packet] {
  def getPacketClass: Class[T]

  def handleClient(packet: T)

  def handleServer(packet: T)
}