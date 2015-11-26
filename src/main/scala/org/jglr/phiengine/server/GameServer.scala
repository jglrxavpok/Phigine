package org.jglr.phiengine.server

import org.jglr.phiengine.core.utils.{Version, ITickable}
import org.jglr.phiengine.network.channels.PhiChannel
import org.jglr.phiengine.network.server.ServerNetHandler
import org.jglr.phiengine.network.utils.ServerStatus
import org.jglr.phiengine.network.{NetworkSide, NetworkHandler}

abstract class GameServer extends ITickable {

  val netHandler = new ServerNetHandler(this)
  val channel = new PhiChannel(NetworkSide.SERVER)
  netHandler.registerChannel(channel)
  registerPackets(netHandler)
  val server = netHandler.startServer(getPort)

  /**
    * Ticks this object
    * @param delta
    * The time in milliseconds between the last two frames
    */
  override def tick(delta: Float): Unit = {

  }

  def registerPackets(netHandler: NetworkHandler): Unit

  def getPort: Int

  def getName: String

  def getPlayerCount: Int

  def getMaxPlayerCount: Int

  def getMotd: String

  def getNetworkVersion: Version = new Version(1,0,0,0)

  def createStatus: ServerStatus = {
    val status = new ServerStatus
    status.playerCount = getPlayerCount
    status.maxPlayerCount = getMaxPlayerCount
    status.motd = getMotd
    status.name = getName
    status.networkingVersion = getNetworkVersion
    status
  }
}
