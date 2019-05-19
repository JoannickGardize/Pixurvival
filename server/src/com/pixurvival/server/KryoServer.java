package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class KryoServer extends Server {

	public KryoServer() {
		super(16384, 16384);
	}

	@Override
	protected Connection newConnection() {
		return new PlayerConnection();
	}
}
