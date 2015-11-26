package org.jglr.phiengine.network.utils

import io.netty.handler.codec.LengthFieldBasedFrameDecoder

class PhiFrameDecoder extends LengthFieldBasedFrameDecoder(PhigineNetSettings.maxPacketSize, 0, 4, 0, 4)
