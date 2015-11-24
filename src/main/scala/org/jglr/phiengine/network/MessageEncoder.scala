package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.network.NetworkSide.NetworkSide

class MessageEncoder(val side: NetworkSide) extends MessageToByteEncoder[Packet] {
  private var buffer: ByteBuf = null

  @throws(classOf[Exception])
  protected def encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
    if (buffer == null) {
      buffer = ctx.alloc.buffer(2 * 1024 * 1024)
    }
    msg.write(buffer)
    val l: Int = buffer.writerIndex
    out.writeBytes(buffer, l)
    buffer.writerIndex(0)
    out.writeInt(l)
    out.writeInt(PhiEngine.getInstance.getNetworkHandler.getPacketID(side, msg))
  }
}