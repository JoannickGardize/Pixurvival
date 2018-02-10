package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.PlayerEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection {

	private boolean logged = false;
	private PlayerEntity playerEntity = null;
}
