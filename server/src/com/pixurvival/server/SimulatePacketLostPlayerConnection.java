package com.pixurvival.server;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimulatePacketLostPlayerConnection extends PlayerConnection {

	private float lostRate;

	@Override
	public int sendUDP(Object object) {
		if (Math.random() > lostRate) {
			return super.sendUDP(object);
		} else {
			return 0;
		}
	}
}
