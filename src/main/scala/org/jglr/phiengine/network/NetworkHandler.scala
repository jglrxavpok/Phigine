package org.jglr.phiengine.network

import com.google.common.collect.Maps
import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.channels.{PhiChannel, NetworkChannel}
import org.jglr.phiengine.core.utils.Registry
import java.lang.reflect.InvocationTargetException
import java.util

abstract class NetworkHandler {
  private final val channels: Registry[String, NetworkChannel] = new Registry[String, NetworkChannel]
  private final val handlers: Registry[Class[_ <: Packet], PacketHandler[_ <: Packet]] = new Registry[Class[_ <: Packet], PacketHandler[_ <: Packet]]
  private final val sidePackets: util.HashMap[NetworkSide, Registry[Integer, Class[_ <: Packet]]] = new util.HashMap()

  sidePackets.put(NetworkSide.CLIENT, new Registry[Integer, Class[_ <: Packet]])
  sidePackets.put(NetworkSide.SERVER, new Registry[Integer, Class[_ <: Packet]])

  def registerPacket[T <: Packet](side: NetworkSide, id: Int, packet: Class[T], handler: PacketHandler[T] = null): Unit = {
    sidePackets.get(side).register(id, packet)
    if(handler != null) {
      registerHandler(packet, handler)
    }
  }

  def registerChannel(channel: NetworkChannel) {
    channels.register(channel.getName, channel)
  }

  def getHandler[T <: Packet](packetClass: Class[T]): PacketHandler[T] = {
    handlers.get(packetClass).asInstanceOf[PacketHandler[T]]
  }

  def registerHandler[T <: Packet](packet: Class[T], handler: PacketHandler[T]) {
    handlers.register(packet, handler)
  }

  @throws(classOf[NoSuchMethodException])
  @throws(classOf[IllegalAccessException])
  @throws(classOf[InvocationTargetException])
  @throws(classOf[InstantiationException])
  def newPacket(side: NetworkSide, id: Int): Packet = {
    val packetClass: Class[_ <: Packet] = sidePackets.get(side).get(id)
    if (packetClass != null) {
      val p: Packet = packetClass.getConstructor(classOf[Int]).newInstance(id.asInstanceOf[Object])
      return p
    }
    null
  }

  def getChannelRegistry: Registry[String, NetworkChannel] = {
    channels
  }

  def getPacketID(side: NetworkSide, packet: Packet): Int = {
    val value = sidePackets.get(side).findKey(packet.getClass)
    if(value != null) {
      value
    } else {
      println("nothing found on side "+side+" for "+packet.getClass)
      -1
    }
  }
}