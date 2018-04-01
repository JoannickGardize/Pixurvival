package com.pixurvival.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.CompressedChunk;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.CraftItemRequest;
import com.pixurvival.core.message.DropItemRequest;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.InteractStructureRequest;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.MissingChunk;
import com.pixurvival.core.message.PlayerActionRequest;
import com.pixurvival.core.message.RequestContentPacks;
import com.pixurvival.core.message.TimeRequest;
import com.pixurvival.core.message.TimeResponse;

class ServerListener extends Listener {

	private List<ClientMessage> clientMessages = new ArrayList<>();
	private Map<Class<?>, Consumer<ClientMessage>> messageActions = new HashMap<>();
	private List<CompressedChunk> chunksToSend = new ArrayList<>();

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
		messageActions.put(InventoryActionRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			PlayerEntity entity = connection.getPlayerEntity();
			if (entity != null) {
				entity.apply((InventoryActionRequest) m.getObject());
			}
		});
		messageActions.put(InteractStructureRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			PlayerEntity entity = connection.getPlayerEntity();
			if (entity != null) {
				entity.apply((InteractStructureRequest) m.getObject());
			}
		});
		messageActions.put(CraftItemRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			PlayerEntity entity = connection.getPlayerEntity();
			if (entity != null) {
				entity.apply((CraftItemRequest) m.getObject());
			}
		});
		messageActions.put(DropItemRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			PlayerEntity entity = connection.getPlayerEntity();
			if (entity != null) {
				entity.apply((DropItemRequest) m.getObject());
			}
		});
		messageActions.put(RequestContentPacks.class, m -> {
			PlayerConnection connection = m.getConnection();
			game.getContentPackUploadManager().sendContentPacks(connection, (RequestContentPacks) m.getObject());
		});
		messageActions.put(GameReady.class, m -> {
			m.getConnection().setGameReady(true);
		});
		messageActions.put(TimeRequest.class, m -> {
			m.getConnection().sendUDP(
					new TimeResponse(((TimeRequest) m.getObject()).getRequesterTime(), System.currentTimeMillis()));
		});
		messageActions.put(MissingChunk.class, m -> {
			PlayerEntity p = m.getConnection().getPlayerEntity();
			chunksToSend.clear();
			TiledMap map = p.getWorld().getMap();
			for (Position position : ((MissingChunk) m.getObject()).getPositions()) {
				Chunk chunk = map.chunkAt(position);
				if (chunk != null) {
					chunksToSend.add(chunk.getCompressed());
				}
			}
			if (!chunksToSend.isEmpty()) {
				m.getConnection().sendUDP(chunksToSend.toArray(new CompressedChunk[chunksToSend.size()]));
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
