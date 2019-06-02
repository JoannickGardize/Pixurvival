package com.pixurvival.client;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.WorldUpdate;

class NetworkMessageHandler extends Listener {

	private Map<Class<?>, Consumer<?>> messageActions = new IdentityHashMap<>(9);

	private List<Object> receivedObjects = new ArrayList<>();

	public NetworkMessageHandler(ClientGame game) {
		putMessageAction(LoginResponse.class, r -> game.notify(l -> l.loginResponse(r)));
		putMessageAction(CreateWorld.class, game::initializeNetworkWorld);
		putMessageAction(ContentPackPart.class, p -> game.getContentPackDownloadManager().accept(p));

		// TODO Download system
		// messageActions.put(RequestContentPacks.class, r -> {
		// game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
		// });
		putMessageAction(TimeResponse.class, o -> {
			TimeResponse t = o;
			game.synchronizeTime(t);
		});
		putMessageAction(PlayerInventory.class, i -> game.getMyInventory().set(i));
		putMessageAction(PlayerData[].class, game::offer);
		putMessageAction(WorldUpdate.class, game::offer);
		putMessageAction(StartGame.class, g -> game.addPlugin(new WorldUpdater()));
		putMessageAction(ChatEntry.class, c -> game.getWorld().getChatManager().received(c));
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
