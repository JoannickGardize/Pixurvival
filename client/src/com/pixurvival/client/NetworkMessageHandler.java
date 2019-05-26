package com.pixurvival.client;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.WorldUpdate;

class NetworkMessageHandler extends Listener {

	private Map<Class<?>, Consumer<?>> messageActions = new IdentityHashMap<>(8);

	private List<Object> receivedObjects = new ArrayList<>();

	public NetworkMessageHandler(ClientGame game) {
		messageActions.put(LoginResponse.class, r -> game.notify(l -> l.loginResponse((LoginResponse) r)));
		messageActions.put(CreateWorld.class, s -> game.initializeNetworkWorld((CreateWorld) s));
		messageActions.put(ContentPackPart.class, p -> game.getContentPackDownloadManager().accept((ContentPackPart) p));

		// TODO Download system
		// messageActions.put(RequestContentPacks.class, r -> {
		// game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
		// });
		messageActions.put(TimeResponse.class, o -> {
			TimeResponse t = (TimeResponse) o;
			game.synchronizeTime(t);
		});
		messageActions.put(PlayerInventory.class, i -> game.getMyInventory().set((Inventory) i));
		messageActions.put(PlayerData[].class, d -> game.offer((PlayerData[]) d));
		messageActions.put(WorldUpdate.class, u -> game.offer((WorldUpdate) u));
		messageActions.put(StartGame.class, g -> game.addPlugin(new WorldUpdater()));
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
}
