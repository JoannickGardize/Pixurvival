package com.pixurvival.server.util;

import com.pixurvival.core.util.CommonMainArgs;

import lombok.Getter;

@Getter
public class ServerMainArgs extends CommonMainArgs {

	private double simulatePacketLossRate = 0;

	private int defaultPort = 7777;
}
