package org.jglr.phiengine.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.util.concurrent.GenericFutureListener
import org.jglr.phiengine.network.channels.NetworkChannel
import org.jglr.phiengine.core.utils.JavaConversions._
import org.jglr.phiengine.network.utils.PhigineNetSettings

class Server(val netHandler: NetworkHandler) extends Runnable {
  private var channel: Channel = null
  private var port: Int = 0
  private var thread: Thread = null


  def start(port: Int) {
    this.port = port
    thread = new Thread(this)
    thread.start()
  }

  override def run(): Unit = {
    val bossGroup: EventLoopGroup = new NioEventLoopGroup
    val workerGroup: EventLoopGroup = new NioEventLoopGroup
    try {
      val b: ServerBootstrap = new ServerBootstrap
      b.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(new ChannelInitializer[SocketChannel]() {
        @throws(classOf[Exception])
        def initChannel(ch: SocketChannel) {
          ch.pipeline.addLast(new LengthFieldBasedFrameDecoder(PhigineNetSettings.maxPacketSize, 0, 4)).addLast(new MessageDecoder).addLast(new MessageEncoder(netHandler, NetworkSide.SERVER))
          netHandler.getChannelRegistry.foreachValue((v: NetworkChannel) => ch.pipeline.addLast(v))
        }
      }).option[Integer](ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, Boolean.box(true))
      val f: ChannelFuture = b.bind(port).addListener(new GenericFutureListener[ChannelFuture] {
        override def operationComplete(future: ChannelFuture): Unit = {
          if(future.isDone) {
            println(": "+future.isSuccess)
            if(future.cause() != null) {
              future.cause().printStackTrace()
            }
          }
        }
      }).sync
      println("START OF SERV")
      channel = f.channel
      channel.closeFuture.sync.addListener(new GenericFutureListener[ChannelFuture] {
        override def operationComplete(future: ChannelFuture): Unit = {
          if(future.isDone) {
            println(": "+future.isSuccess)
            if(future.cause() != null) {
              future.cause().printStackTrace()
            }
          }
        }
      })
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      workerGroup.shutdownGracefully
      bossGroup.shutdownGracefully
    }
  }
}