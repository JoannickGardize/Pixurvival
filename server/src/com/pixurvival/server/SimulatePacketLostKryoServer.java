package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class SimulatePacketLostKryoServer extends Server {
	private float lostRate;

	public SimulatePacketLostKryoServer(float lostRate) {
		super(16384, 16384);
		this.lostRate = lostRate;
	}

	@Override
	protected Connection newConnection() {
		return new SimulatePacketLostPlayerConnection(lostRate);
	}
}