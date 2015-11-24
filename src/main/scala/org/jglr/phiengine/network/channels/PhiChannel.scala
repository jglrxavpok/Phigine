package org.jglr.phiengine.network.channels

import org.jglr.phiengine.network.NetworkSide.NetworkSide

class PhiChannel(side: NetworkSide) extends NetworkChannel("PhigineDedicatedChannel", side)