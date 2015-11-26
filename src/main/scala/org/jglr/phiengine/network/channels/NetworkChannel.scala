package org.jglr.phiengine.network.channels

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandler}
import io.netty.util.concurrent.GenericFutureListener
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.client.ClientNetHandler
import org.jglr.phiengine.network.server.ServerNetHandler
import org.jglr.phiengine.network._

class NetworkChannel(private val name: String, private val side: NetworkSide) extends ChannelInboundHandler {
  private var context: ChannelHandlerContext = null

  def getName: String = {
    name
  }

  def getContext: ChannelHandlerContext = {
    context
  }

  def writeFlush(packet: Packet): Unit = {
    write(packet)
    flush()
  }

  def write(packet: Packet): Unit = {
    packet.setChannel(name)
    //.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
    context.write(packet).addListener(new GenericFutureListener[ChannelFuture] {
      override def operationComplete(future: ChannelFuture): Unit = {
        if(future.isDone) {
          println(": "+future.isSuccess)
          if(future.cause() != null) {
            future.cause().printStackTrace()
          }
        }
      }
    })
    println("write")
  }

  def flush(): Unit = {
    context.flush
  }

  def onConnection(): Unit = {
  }

  @throws(classOf[Exception])
  def channelRegistered(ctx: ChannelHandlerContext) {
  }

  @throws(classOf[Exception])
  def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
  }

  @throws(classOf[Exception])
  def channelActive(ctx: ChannelHandlerContext) {
    this.context = ctx
    onConnection()
  }

  @throws(classOf[Exception])
  def channelInactive(ctx: ChannelHandlerContext): Unit = {
  }

  @throws(classOf[Exception])
  def channelRead(ctx: ChannelHandlerContext, msg: AnyRef) {
    val message: Message = msg.asInstanceOf[Message]
    if(message.channel.equals(this)) {
      val packet: Packet = message.createPacket
      packet.read(message.payload)
      if (message.getSide.id != side.id) { // check if packet was not sent from a client to a client or from a server to a server
        val handler: PacketHandler[Packet] = message.networkHandler.getHandler(packet.getClass).asInstanceOf[PacketHandler[Packet]]
        if (side == NetworkSide.CLIENT) {
          handler.handleClient(packet, this, message.networkHandler.asInstanceOf[ClientNetHandler])
        } else {
          handler.handleServer(packet, this, message.networkHandler.asInstanceOf[ServerNetHandler])
        }
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

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case null =>
        false

      case channel: NetworkChannel =>
        channel.side.id == side.id && channel.name.equals(name)

      case _ =>
        false
    }
  }
}