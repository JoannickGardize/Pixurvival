package fr.sharkhendrix.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;

import fr.sharkhendrix.pixurvival.core.PlayerEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection {

	private boolean logged = false;
	private PlayerEntity playerEntity = null;
}
