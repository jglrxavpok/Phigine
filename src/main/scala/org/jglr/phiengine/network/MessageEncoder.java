package org.jglr.phiengine.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jglr.phiengine.core.PhiEngine;

public class MessageEncoder extends MessageToByteEncoder<Packet> {

    private final NetworkSide side;
    private ByteBuf buffer;

    public MessageEncoder(NetworkSide side) {
        this.side = side;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        if(buffer == null) {
            buffer = ctx.alloc().buffer(2*1024*1024);
        }

        msg.write(buffer);
        int l = buffer.writerIndex();
        out.writeBytes(buffer, l);
        buffer.writerIndex(0);

        out.writeInt(l);
        out.writeInt(PhiEngine.getInstance().getNetworkHandler().getPacketID(side, msg));

    }
}
