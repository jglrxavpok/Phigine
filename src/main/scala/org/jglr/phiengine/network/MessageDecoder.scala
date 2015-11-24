package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.util

import org.jglr.phiengine.network.NetworkSide.NetworkSide

class MessageDecoder extends ByteToMessageDecoder {
  @throws(classOf[Exception])
  protected def decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: util.List[AnyRef]) {
    val length: Int = msg.readInt
    val id: Int = msg.readInt
    val side: NetworkSide = NetworkSide.get(msg.readByte())
    val message: Message = new Message(side, id)
    message.length = length
    val channelNameLength: Int = msg.readInt
    val chars: Array[Byte] = new Array[Byte](channelNameLength)
    msg.readBytes(chars)
    message.channel = new String(chars, "UTF-8")
    message.payload = msg.readBytes(length)
    out.add(message)
  }
}