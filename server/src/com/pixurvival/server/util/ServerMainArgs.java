package com.pixurvival.server.util;

import com.pixurvival.core.util.CommonMainArgs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerMainArgs extends CommonMainArgs {

    private float simulatePacketLossRate = 0;

    private int port = 7777;
}
