package com.pixurvival.server;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.ContentPackRequest;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RefreshRequest;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.ChooseRoleRequest;
import com.pixurvival.core.message.lobby.ContentPackReady;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.core.message.lobby.RefuseContentPack;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.core.message.playerRequest.CloseInteractionDialogRequest;
import com.pixurvival.core.message.playerRequest.CraftItemRequest;
import com.pixurvival.core.message.playerRequest.DialogInteractionActionRequest;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.core.message.playerRequest.EquipmentActionRequest;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Getter;

class NetworkMessageHandler extends Listener {

	private static class DisconnectedMessage {
		private static final @Getter DisconnectedMessage instance = new DisconnectedMessage();
	}

	private List<ClientMessage> clientMessages = new ArrayList<>();
	private Map<Class<?>, Consumer<ClientMessage>> messageActions = new IdentityHashMap<>(26);

	public NetworkMessageHandler(PixurvivalServer game) {
		messageActions.put(LoginRequest.class, m -> {
			PlayerConnection connection = m.getConnection();
			if (connection.isLogged()) {
				connection.sendTCP(new LoginResponse("You are already logged to this server."));
				return;
			}
			LoginRequest loginRequest = (LoginRequest) m.getObject();
			String name = loginRequest.getPlayerName().trim();
			if (game.getPlayerConnection(name) != null) {
				connection.sendTCP(new LoginResponse("This name is already in use."));
				return;
			}
			if (name.length() > 30) {
				connection.sendTCP(new LoginResponse("The name must not exceed 30 characters."));
				return;
			}
			if (!ReleaseVersion.actual().name().equals(loginRequest.getGameVersion())) {
				connection.sendTCP(new LoginResponse("The server version is " + ReleaseVersion.actual().name() + ", but your game version is " + loginRequest.getGameVersion()));
				return;
			}
			connection.setLogged(true);
			connection.setName(name);
			game.addPlayerConnection(connection);
			game.playerLoggedIn(connection);

		});
		messageActions.put(PlayerMovementRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(PlaceStructureRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(InventoryActionRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(InteractStructureRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(EquipmentActionRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(DropItemRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(CraftItemRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(DialogInteractionActionRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(PlayerEquipmentAbilityRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(CloseInteractionDialogRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(ClientStream.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleClientStream((ClientStream) m.getObject())));
		messageActions.put(UseItemRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(ChatRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handlePlayerActionRequest((IPlayerActionRequest) m.getObject())));
		messageActions.put(GameReady.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleGameReady((GameReady) m.getObject())));
		messageActions.put(RefreshRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleRefreshRequest((RefreshRequest) m.getObject())));
		messageActions.put(CreateTeamRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((CreateTeamRequest) m.getObject())));
		messageActions.put(RenameTeamRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((RenameTeamRequest) m.getObject())));
		messageActions.put(RemoveTeamRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((RemoveTeamRequest) m.getObject())));
		messageActions.put(ChangeTeamRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((ChangeTeamRequest) m.getObject())));
		messageActions.put(ReadyRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((ReadyRequest) m.getObject())));
		messageActions.put(ChooseGameModeRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((LobbyMessage) m.getObject())));
		messageActions.put(ChooseRoleRequest.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((LobbyMessage) m.getObject())));
		messageActions.put(RefuseContentPack.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((RefuseContentPack) m.getObject())));
		messageActions.put(ContentPackReady.class, m -> m.getConnection().getPlayerConnectionListeners().forEach(l -> l.handleLobbyMessage((ContentPackReady) m.getObject())));
		messageActions.put(ContentPackRequest.class, m -> game.getContentPackUploader().sendContentPack(m.getConnection(), ((ContentPackRequest) m.getObject()).getIdentifier()));
		messageActions.put(DisconnectedMessage.class, m -> {
			game.removePlayerConnection(m.getConnection().toString());
			m.getConnection().disconnected();
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
	public void disconnected(Connection connection) {
		synchronized (clientMessages) {
			clientMessages.add(new ClientMessage((PlayerConnection) connection, DisconnectedMessage.getInstance()));
		}
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
