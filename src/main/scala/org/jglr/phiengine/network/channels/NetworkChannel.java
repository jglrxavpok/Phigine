package org.jglr.phiengine.network.channels;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import org.jglr.phiengine.network.NetworkSide;
import org.jglr.phiengine.core.PhiEngine;
import org.jglr.phiengine.network.Message;
import org.jglr.phiengine.network.Packet;
import org.jglr.phiengine.network.PacketHandler;

public class NetworkChannel implements ChannelInboundHandler {

    private final String name;
    private final NetworkSide side;
    private ChannelHandlerContext context;

    public NetworkChannel(String name, NetworkSide side) {
        this.name = name;
        this.side = side;
    }

    public String getName() {
        return name;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void write(Packet packet) {
        packet.setChannel(name);
        context.write(packet); // TODO: Add listener
    }

    public void flush() {
        context.flush();
    }

    public void onConnection() {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        onConnection();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message)msg;
        Packet packet = message.createPacket();
        packet.read(message.payload);
        if(message.getSide() == side) {
            PacketHandler handler = PhiEngine.getInstance().getNetworkHandler().getHandler(packet.getClass());
            if (side.isClient()) {
                handler.handleClient(packet);
            } else {
                handler.handleServer(packet);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        PhiEngine.crash("Error while reading network", cause);
    }
}
