package com.pixurvival.client;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.message.ContentPackCheck;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerDead;
import com.pixurvival.core.message.Spectate;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TimeSync;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.GameModeList;
import com.pixurvival.core.message.lobby.LobbyData;

class NetworkMessageHandler extends Listener {

	private Map<Class<?>, Consumer<?>> messageActions = new IdentityHashMap<>(9);

	private List<Object> receivedObjects = new ArrayList<>();

	public NetworkMessageHandler(PixurvivalClient game) {
		putMessageAction(LoginResponse.class, r -> game.notify(l -> l.loginResponse(r)));
		putMessageAction(CreateWorld.class, game::initializeNetworkWorld);
		putMessageAction(ContentPackPart.class, p -> game.getContentPackDownloadManager().accept(p));

		// TODO Download system
		// messageActions.put(RequestContentPacks.class, r -> {
		// game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
		// });
		putMessageAction(TimeSync.class, o -> {
			TimeSync t = o;
			game.synchronizeTime(t);
		});
		putMessageAction(PlayerInventory.class, i -> {
			game.getMyInventory().set(i);
		});
		putMessageAction(WorldUpdate.class, game::offer);
		putMessageAction(StartGame.class, g -> {
			game.addPlugin(new WorldUpdater());
			game.getWorld().getTime().setTimeMillis(g.getWorldTime());
		});
		putMessageAction(ChatEntry.class, c -> game.getWorld().getChatManager().received(c));
		putMessageAction(Spectate.class, game::spectate);
		putMessageAction(PlayerDead[].class, pd -> {
			for (PlayerDead playerDead : pd) {
				PlayerEntity player = game.getWorld().getPlayerEntities().get(playerDead.getId());
				player.getTeam().addDead(player);
			}
		});
		putMessageAction(EndGameData.class, game::notifyGameEnded);
		putMessageAction(EnterLobby.class, e -> game.notify(ClientGameListener::enterLobby));
		putMessageAction(LobbyData.class, ll -> game.notify(l -> l.lobbyMessageReceived(ll)));
		putMessageAction(GameModeList.class, gml -> game.notify(l -> l.lobbyMessageReceived(gml)));
		putMessageAction(ContentPackCheck.class, cpc -> game.checkContentPackValidity(cpc));
	}

	@Override
	public void disconnected(Connection connection) {
	}

	@Override
	public void received(Connection connection, Object object) {
		synchronized (receivedObjects) {
			if (object != null) {
				receivedObjects.add(object);
			}
		}
	}

	public void consumeReceivedObjects() {
		synchronized (receivedObjects) {
			for (Object object : receivedObjects) {
				@SuppressWarnings("unchecked")
				Consumer<Object> action = (Consumer<Object>) messageActions.get(object.getClass());
				if (action != null) {
					action.accept(object);
				} else {
					Log.warn("Received unknown object type  : " + object.getClass().getSimpleName());
				}
			}
			receivedObjects.clear();
		}
	}

	private <T> void putMessageAction(Class<T> type, Consumer<T> action) {
		messageActions.put(type, action);
	}
}
