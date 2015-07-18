package org.jglr.phiengine.network;

import com.google.common.collect.Maps;
import org.jglr.phiengine.network.channels.NetworkChannel;
import org.jglr.phiengine.core.PhiEngine;
import org.jglr.phiengine.core.utils.Registry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class NetworkHandler {

    private final PhiEngine engine;
    private final Registry<String, NetworkChannel> channels;
    private final Registry<Class<? extends Packet>, PacketHandler<? extends Packet>> handlers;
    private final HashMap<NetworkSide, Registry<Integer, Class<? extends Packet>>> sidePackets;

    public NetworkHandler(PhiEngine engine) {
        this.engine = engine;
        channels = new Registry<>();
        handlers = new Registry<>();
        sidePackets = Maps.newHashMap();
        sidePackets.put(NetworkSide.CLIENT, new Registry<>());
        sidePackets.put(NetworkSide.SERVER, new Registry<>());
    }

    public void registerPacket(NetworkSide side, int id, Class<? extends Packet> packet) {
        sidePackets.get(side).register(id, packet);
    }

    public void registerChannel(String name, NetworkChannel channel) {
        channels.register(name, channel);
    }

    public <T extends Packet> PacketHandler<T> getHandler(Class<T> packetClass) {
        return (PacketHandler<T>) handlers.get(packetClass);
    }

    public <T extends Packet> void registerHandler(Class<T> packet, PacketHandler<T> handler) {
        handlers.register(packet, handler);
    }

    public Packet newPacket(NetworkSide side, int id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends Packet> packetClass = sidePackets.get(side).get(id);
        if(packetClass != null) {
            Packet p = packetClass.getConstructor().newInstance();
            return p;
        }
        return null;
    }

    public Server newServer() {
        Server server = new Server(this);
        return server;
    }

    public Registry<String, NetworkChannel> getChannelRegistry() {
        return channels;
    }

    public int getPacketID(NetworkSide side, Packet packet) {
        return sidePackets.get(side).findKey(packet.getClass());
    }
}
