package org.jglr.phiengine.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.util

import org.jglr.phiengine.network.NetworkSide.NetworkSide

class MessageDecoder(val networkHandler: NetworkHandler) extends ByteToMessageDecoder {
  @throws(classOf[Exception])
  protected def decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: util.List[AnyRef]) {
    if(msg.readableBytes() == 0)
      return
    val packetIndex: Long = msg.readLong()
    val answerIndex: Long = msg.readLong()
    val length: Int = msg.readInt
    val id: Int = msg.readInt
    val side: NetworkSide = NetworkSide.get(msg.readByte())
    val message: Message = new Message(side, id)
    message.answerIndex = answerIndex
    message.packetIndex = packetIndex
    message.length = length
    val channelNameLength: Int = msg.readInt
    val chars: Array[Byte] = new Array[Byte](channelNameLength)
    msg.readBytes(chars)
    message.networkHandler = networkHandler
    val channelName = new String(chars, "UTF-8")
    message.channel = networkHandler.getChannelRegistry.get(channelName)
    println(s"found name: $channelName")
    message.payload = msg.readBytes(length)
    out.add(message)
  }
}