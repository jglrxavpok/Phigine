package org.jglr.phiengine.server

import org.jglr.phiengine.core.utils.ITickable
import org.jglr.phiengine.network.channels.PhiChannel
import org.jglr.phiengine.network.{NetworkSide, NetworkHandler}

abstract class GameServer extends ITickable {

  val netHandler = new NetworkHandler
  val channel = new PhiChannel(NetworkSide.SERVER)
  netHandler.getChannelRegistry.register("default", channel)
  registerPackets(netHandler)
  val server = netHandler.newServer
  start(getPort)

  def start(port: Int): Unit = {
    server.start(port)
  }

  /**
    * Ticks this object
    * @param delta
   * The time in milliseconds between the last two frames
    */
  override def tick(delta: Float): Unit = {

  }

  def registerPackets(netHandler: NetworkHandler): Unit

  def getPort: Int
}
