package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.core.PhiEngine
import java.lang.reflect.InvocationTargetException

import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.channels.NetworkChannel

class Message(val side: NetworkSide, val id: Int) {
  var payload: ByteBuf = null
  var length: Int = 0
  var channel: NetworkChannel = null
  var networkHandler: NetworkHandler = null
  var packetIndex: Long = -1L
  var answerIndex: Long = -1L

  def getID: Int = {
    id
  }

  def getSide: NetworkSide = {
    side
  }

  @throws(classOf[InvocationTargetException])
  @throws(classOf[NoSuchMethodException])
  @throws(classOf[InstantiationException])
  @throws(classOf[IllegalAccessException])
  def createPacket: Packet = {
    val packet = networkHandler.newPacket(side, id)
    packet.packetIndex = packetIndex
    packet.answerIndex = answerIndex
    packet
  }
}