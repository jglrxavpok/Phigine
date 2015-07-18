package org.jglr.phiengine.network;

import io.netty.buffer.ByteBuf;
import org.jglr.phiengine.core.PhiEngine;

import java.lang.reflect.InvocationTargetException;

public class Message {

    private final NetworkSide side;
    private final int id;
    public ByteBuf payload;
    public int length;
    public String channel;

    public Message(NetworkSide side, int id) {
        this.side = side;
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public NetworkSide getSide() {
        return side;
    }

    public Packet createPacket() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        NetworkHandler net = PhiEngine.getInstance().getNetworkHandler();
        return net.newPacket(side, id);
    }
}
