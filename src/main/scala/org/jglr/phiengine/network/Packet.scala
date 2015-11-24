package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf

abstract class Packet(val id: Int) {
  private var channel: String = null

  def write(buffer: ByteBuf)

  def read(buffer: ByteBuf)

  def getChannel: String = {
    channel
  }

  def setChannel(channel: String) {
    this.channel = channel
  }
}