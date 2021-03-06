package org.jglr.phiengine.network.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.concurrent.GenericFutureListener
import org.jglr.phiengine.core.utils.JavaConversions._
import org.jglr.phiengine.network.channels.NetworkChannel
import org.jglr.phiengine.network.utils.PhiFrameDecoder
import org.jglr.phiengine.network.{MessageDecoder, MessageEncoder, NetworkHandler, NetworkSide}

class Client(val netHandler: ClientNetHandler) extends Runnable {
  private var channel: Channel = null
  private var host: String = null
  private var port: Int = 0
  private var thread: Thread = null

  private val bossGroup: EventLoopGroup = new NioEventLoopGroup
  private val workerGroup: EventLoopGroup = new NioEventLoopGroup

  def start(host: String, port: Int) {
    this.host = host
    this.port = port

    try {
      val b: Bootstrap = new Bootstrap()
      b.group(workerGroup).channel(classOf[NioSocketChannel]).handler(new ChannelInitializer[SocketChannel]() {
        @throws(classOf[Exception])
        def initChannel(ch: SocketChannel) {
          val decoder = new MessageDecoder(netHandler)
          val encoder = new MessageEncoder(netHandler, NetworkSide.CLIENT)
          val framer = new PhiFrameDecoder()
          ch.pipeline.addLast(framer).addLast(decoder).addLast(encoder)
          netHandler.getChannelRegistry.foreachValue((v: NetworkChannel) => ch.pipeline.addLast(v))
        }
      }).option(ChannelOption.SO_KEEPALIVE, Boolean.box(true))
      val future: ChannelFuture = b.connect(host, port)
      future.addListener(new GenericFutureListener[ChannelFuture] {
        override def operationComplete(future: ChannelFuture): Unit = {
          if(future.isDone) {
            if(future.cause() != null) {
              println(":c")
              future.cause().printStackTrace()
            }
          }
        }
      })
      future.sync()
      channel = future.channel()
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    }

    thread = new Thread(this)
    thread.start()
  }

  override def run(): Unit = {
    channel.closeFuture.sync.addListener(new GenericFutureListener[ChannelFuture] {
      override def operationComplete(future: ChannelFuture): Unit = {
        if(future.isDone) {
          if(future.cause() != null) {
            println("he ded :c")
            future.cause().printStackTrace()
          }
        }
      }
    })
    workerGroup.shutdownGracefully
    bossGroup.shutdownGracefully
  }
}