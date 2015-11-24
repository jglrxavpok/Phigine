package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.core.PhiEngine
import java.lang.reflect.InvocationTargetException

import org.jglr.phiengine.network.NetworkSide.NetworkSide

class Message(val side: NetworkSide, val id: Int) {
  var payload: ByteBuf = null
  var length: Int = 0
  var channel: String = null

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
    val net: NetworkHandler = PhiEngine.getInstance.getNetworkHandler
    net.newPacket(side, id)
  }
}