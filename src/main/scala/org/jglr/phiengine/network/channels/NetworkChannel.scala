package org.jglr.phiengine.network.channels

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandler
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.{Message, Packet}

class NetworkChannel(private val name: String, private val side: NetworkSide) extends ChannelInboundHandler {
  private var context: ChannelHandlerContext = null

  def getName: String = {
    name
  }

  def getContext: ChannelHandlerContext = {
    context
  }

  def write(packet: Packet) {
    packet.setChannel(name)
    context.write(packet)
  }

  def flush(): Unit = {
    context.flush
  }

  def onConnection(): Unit = {
  }

  @throws(classOf[Exception])
  def channelRegistered(ctx: ChannelHandlerContext) {
    this.context = ctx
  }

  @throws(classOf[Exception])
  def channelUnregistered(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def channelActive(ctx: ChannelHandlerContext) {
    onConnection()
  }

  @throws(classOf[Exception])
  def channelInactive(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def channelRead(ctx: ChannelHandlerContext, msg: AnyRef) {
    val message: Message = msg.asInstanceOf[Message]
    val packet: Packet = message.createPacket
    packet.read(message.payload)
    if (message.getSide == side) {
      val handler = PhiEngine.getInstance.getNetworkHandler.getHandler(packet.getClass)
      if (side.isClient) {
        handler.handleClient(packet)
      }
      else {
        handler.handleServer(packet)
      }
    }
  }

  @throws(classOf[Exception])
  def channelReadComplete(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def userEventTriggered(ctx: ChannelHandlerContext, evt: AnyRef) {
  }

  @throws(classOf[Exception])
  def channelWritabilityChanged(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def handlerAdded(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def handlerRemoved(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    PhiEngine.crash("Error while reading network", cause)
  }
}