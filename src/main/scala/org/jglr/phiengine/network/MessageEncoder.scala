package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.network.NetworkSide.NetworkSide
import org.jglr.phiengine.network.utils.PhigineNetSettings

class MessageEncoder(val netHandler: NetworkHandler, val side: NetworkSide) extends MessageToByteEncoder[Packet] {
  private var buffer: ByteBuf = null
  private var frame: ByteBuf = null

  @throws(classOf[Exception])
  protected def encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
    if (buffer == null) {
      buffer = ctx.alloc.buffer(PhigineNetSettings.maxPacketSize)
      frame = ctx.alloc.buffer(PhigineNetSettings.maxPacketSize)
    }
    buffer.writerIndex(0)
    frame.writerIndex(0)
    msg.write(buffer)
    val l: Int = buffer.writerIndex
    frame.writeInt(l)
    frame.writeInt(netHandler.getPacketID(side, msg))
    frame.writeByte(side.id)

    val channel = msg.getChannel
    frame.writeInt(channel.length)
    frame.writeBytes(channel.getBytes("UTF-8"))
    frame.writeBytes(buffer)

    val frameLength = frame.writerIndex()
    out.writeInt(frameLength)
    out.writeBytes(frame)
  }
}