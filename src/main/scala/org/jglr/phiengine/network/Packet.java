package org.jglr.phiengine.network;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
    private String channel;

    public abstract void write(ByteBuf buffer);

    public abstract void read(ByteBuf buffer);

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
