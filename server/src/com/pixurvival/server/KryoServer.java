package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Server;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.message.WorldUpdate;

public class KryoServer extends Server {

    public KryoServer() {
        super(WorldUpdate.BUFFER_SIZE * 2, WorldUpdate.BUFFER_SIZE * 2, new KryoSerialization(new WorldKryo()));
    }

    @Override
    protected Connection newConnection() {
        return new PlayerConnection();
    }
}
