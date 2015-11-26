package org.jglr.phiengine.network.server

import org.jglr.phiengine.network.NetworkHandler
import org.jglr.phiengine.server.GameServer

class ServerNetHandler(val backing: GameServer) extends NetworkHandler {

  val server = new Server(backing, this)

  def startServer(port: Int): Server = {
    server.start(port)
    server
  }
}
