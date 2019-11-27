package com.pixurvival.server;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RefreshRequest;
import com.pixurvival.core.message.RequestContentPacks;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TimeRequest;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.core.message.playerRequest.CraftItemRequest;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.core.message.playerRequest.EquipmentActionRequest;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;

class NetworkMessageHandler extends Listener {

	private List<ClientMessage> clientMessages = new ArrayList<>();
	private Map<Class<?>, Consumer<ClientMessage>> messageActions = new IdentityHashMap<>(15);
	private ServerGame game;

	public NetworkMessageHandler(ServerGame game) {
		this.game = game;
		messageActions.put(LoginRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			if (connection.isLogged()) {
				connection.sendTCP(LoginResponse.ALREADY_LOGGED);
				return;
			}
			String name = ((LoginRequest) m.getObject()).getPlayerName();
			if (game.getPlayerConnection(name) != null) {
				connection.sendTCP(LoginResponse.NAME_IN_USE);
				return;
			}
			connection.setLogged(true);
			connection.setName(name);
			game.addPlayerConnection(connection);
			game.notify(l -> l.playerLoggedIn(connection));
		});
		messageActions.put(PlayerMovementRequest.class, this::handlePlayerActionRequest);
		messageActions.put(PlaceStructureRequest.class, this::handlePlayerActionRequest);
		messageActions.put(InventoryActionRequest.class, this::handlePlayerActionRequest);
		messageActions.put(InteractStructureRequest.class, this::handlePlayerActionRequest);
		messageActions.put(EquipmentActionRequest.class, this::handlePlayerActionRequest);
		messageActions.put(DropItemRequest.class, this::handlePlayerActionRequest);
		messageActions.put(CraftItemRequest.class, this::handlePlayerActionRequest);
		messageActions.put(PlayerEquipmentAbilityRequest.class, this::handlePlayerActionRequest);
		messageActions.put(ClientStream.class, this::handleClientStream);
		messageActions.put(UseItemRequest.class, this::handlePlayerActionRequest);
		messageActions.put(ChatRequest.class, this::handlePlayerActionRequest);
		messageActions.put(RequestContentPacks.class, m -> {
			PlayerConnection connection = m.getConnection();
			game.getContentPackUploadManager().sendContentPacks(connection, (RequestContentPacks) m.getObject());
		});
		messageActions.put(GameReady.class, m -> {
			m.getConnection().setGameReady(true);
			if (m.getConnection().isReconnected()) {
				m.getConnection().sendTCP(new StartGame());
			}
		});
		messageActions.put(TimeRequest.class,
				m -> m.getConnection().sendUDP(new TimeResponse(((TimeRequest) m.getObject()).getRequesterTime(), m.getConnection().getPlayerEntity().getWorld().getTime().getTimeMillis())));
		messageActions.put(RefreshRequest.class, m -> m.getConnection().setRequestedFullUpdate(true));

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
		game.removePlayerConnection(connection.toString());
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object != null) {
			synchronized (clientMessages) {
				clientMessages.add(new ClientMessage((PlayerConnection) connection, object));
			}
		}
	}

	private void handlePlayerActionRequest(ClientMessage m) {
		PlayerConnection connection = m.getConnection();
		PlayerEntity entity = connection.getPlayerEntity();
		if (entity != null && entity.isAlive()) {
			((IPlayerActionRequest) m.getObject()).apply(entity);
		}
	}

	private void handleClientStream(ClientMessage m) {
		ClientStream clientStream = (ClientStream) m.getObject();
		PlayerConnection connection = m.getConnection();
		PlayerEntity playerEntity = connection.getPlayerEntity();
		ClientAckManager.getInstance().acceptAcks(connection, clientStream.getAcks());
		if (clientStream.getTime() > connection.getPreviousClientWorldTime()) {
			connection.setPreviousClientWorldTime(clientStream.getTime());
			playerEntity.getTargetPosition().set(clientStream.getTargetPosition());
		}
	}
}
