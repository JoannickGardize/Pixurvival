package fr.sharkhendrix.pixurvival.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import fr.sharkhendrix.pixurvival.core.PlayerEntity;
import fr.sharkhendrix.pixurvival.core.message.LoginRequest;
import fr.sharkhendrix.pixurvival.core.message.LoginResponse;
import fr.sharkhendrix.pixurvival.core.message.PlayerActionRequest;

class ServerListener extends Listener {

	private List<ClientMessage> clientMessages = new ArrayList<>();
	private Map<Class<?>, Consumer<ClientMessage>> messageActions = new HashMap<>();

	public ServerListener(ServerGame game) {
		messageActions.put(LoginRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			if (connection.isLogged()) {
				connection.sendTCP(LoginResponse.ALREADY_LOGGED);
				return;
			}
			connection.setLogged(true);
			connection.setName(((LoginRequest) m.getObject()).getPlayerName());
			game.notify(l -> l.playerLoggedIn(connection));
		});
		messageActions.put(PlayerActionRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			PlayerEntity entity = connection.getPlayerEntity();
			if (entity != null) {
				entity.apply((PlayerActionRequest) m.getObject());
			}
		});
	}

	public void consumeReceivedObjects() {
		synchronized (clientMessages) {
			for (ClientMessage clientMessage : clientMessages) {
				Consumer<ClientMessage> action = messageActions.get(clientMessage.getObject().getClass());
				if (action != null) {
					action.accept(clientMessage);
				}
			}
			clientMessages.clear();
		}
	}

	@Override
	public void connected(Connection connection) {

	}

	@Override
	public void disconnected(Connection connection) {

	}

	@Override
	public void received(Connection connection, Object object) {
		if (object != null) {
			synchronized (clientMessages) {
				clientMessages.add(new ClientMessage((PlayerConnection) connection, object));
			}
		}
	}
}
