package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Server;
import com.pixurvival.core.message.WorldKryo;

public class SimulatePacketLostKryoServer extends Server {
	private float lostRate;

	public SimulatePacketLostKryoServer(float lostRate) {
		super(16384, 16384, new KryoSerialization(new WorldKryo()));
		this.lostRate = lostRate;
	}

	@Override
	protected Connection newConnection() {
		return new SimulatePacketLostPlayerConnection(lostRate);
	}
}
