package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class KryoServer extends Server {

	@Override
	protected Connection newConnection() {
		return new PlayerConnection();
	}
}
