package org.jglr.phiengine.network.client

import org.jglr.phiengine.network.NetworkHandler

class ClientNetHandler extends NetworkHandler {

  val client = new Client(this)

  def startClient(host: String, port: Int): Client = {
    client.start(host, port)
    client
  }
}
