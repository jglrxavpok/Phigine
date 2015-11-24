package org.jglr.phiengine.server

import org.jglr.phiengine.core.utils.ITickable
import org.jglr.phiengine.network.{NetworkSide, NetworkHandler}

class GameServer extends ITickable {

  val netHandler = new NetworkHandler
  val server = netHandler.newServer

  def start(port: Int): Unit = {

  }

  /**
    * Ticks this object
    * @param delta
   * The time in milliseconds between the last two frames
    */
  override def tick(delta: Float): Unit = {

  }
}
