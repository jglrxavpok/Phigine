package org.jglr.phiengine.network.channels;

import org.jglr.phiengine.network.NetworkSide;

public class PhiChannel extends NetworkChannel {

    public PhiChannel(NetworkSide side) {
        super("PhiEngineDedicatedChannel", side);
    }

}
