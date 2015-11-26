package org.jglr.phiengine.network.channels

import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandler}
import io.netty.util.concurrent.GenericFutureListener
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.{PacketHandler, Message, Packet}

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
    println("channelRead!! :DDD")
    val message: Message = msg.asInstanceOf[Message]
    val packet: Packet = message.createPacket
    packet.read(message.payload)
    if (message.getSide == side) {
      val handler: PacketHandler[Packet] = PhiEngine.getInstance.getNetworkHandler.getHandler(packet.getClass).asInstanceOf[PacketHandler[Packet]]
      if (side.isClient) {
        handler.handleClient(packet)
      } else {
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