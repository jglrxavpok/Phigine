package org.jglr.phiengine.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readInt();
        int id = msg.readInt();
        NetworkSide side = NetworkSide.values()[msg.readByte()];
        Message message = new Message(side, id);
        message.length = length;
        int channelNameLength = msg.readInt();
        byte[] chars = new byte[channelNameLength];
        msg.readBytes(chars);
        message.channel = new String(chars, "UTF-8");
        message.payload = msg.readBytes(length);
        out.add(message);
    }
}
