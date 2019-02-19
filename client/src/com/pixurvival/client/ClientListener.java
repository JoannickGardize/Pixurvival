package com.pixurvival.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.TimeResponse;
import com.pixurvival.core.message.WorldUpdate;

class ClientListener extends Listener {

	private ClientGame game;

	private Map<Class<?>, Consumer<?>> messageActions = new HashMap<>();

	private List<Object> receivedObjects = new ArrayList<>();

	public ClientListener(ClientGame game) {
		this.game = game;
		messageActions.put(LoginResponse.class, r -> game.notify(l -> l.loginResponse((LoginResponse) r)));
		messageActions.put(InitializeGame.class, s -> {
			game.setInitGame((InitializeGame) s);
		});
		messageActions.put(ContentPackPart.class,
				p -> game.getContentPackDownloadManager().accept((ContentPackPart) p));

		// TODO Download system
		// messageActions.put(RequestContentPacks.class, r -> {
		// game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
		// });
		messageActions.put(TimeResponse.class, o -> {
			TimeResponse t = (TimeResponse) o;
			long time = (System.currentTimeMillis() - t.getRequesterTime()) / 2;
			game.updatePing(time);
		});
		messageActions.put(Inventory.class, i -> {
			game.getMyInventory().set((Inventory) i);
		});
		messageActions.put(PlayerData[].class, d -> {
			game.addPlayerData((PlayerData[]) d);
		});
	}

	@Override
	public void disconnected(Connection connection) {
		// TODO Auto-generated method stub
	}

	@Override
	public void received(Connection connection, Object object) {
		synchronized (receivedObjects) {
			Log.debug(object.toString());
			if (object != null && !(object instanceof WorldUpdate)) {
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
				}
			}
			receivedObjects.clear();
		}
	}
}
