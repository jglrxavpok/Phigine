package org.jglr.phiengine.network;

public interface PacketHandler<T extends Packet> {

    Class<T> getPacketClass();

    void handleClient(T packet);

    void handleServer(T packet);
}
