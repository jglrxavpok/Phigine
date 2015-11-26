package org.jglr.phiengine.network.packets.client

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.network.Packet
import org.jglr.phiengine.network.packets.EmptyPacket

class PacketAskStatus(id: Int) extends EmptyPacket(id)
