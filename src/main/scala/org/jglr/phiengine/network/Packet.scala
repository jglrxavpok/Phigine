package org.jglr.phiengine.network

import org.jglr.phiengine.network.utils.NetworkSerializable

abstract class Packet(val id: Int) extends NetworkSerializable {
  private var channel: String = null

  def getChannel: String = {
    channel
  }

  def setChannel(channel: String) {
    this.channel = channel
  }
}