package com.pixurvival.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.EntitiesUpdate;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RequestContentPacks;
import com.pixurvival.core.message.StartGame;

class ClientListener extends Listener {

	private ClientGame game;

	private Map<Class<?>, Consumer<?>> messageActions = new HashMap<>();

	private List<Object> receivedObjects = new ArrayList<>();

	public ClientListener(ClientGame game) {
		this.game = game;
		messageActions.put(LoginResponse.class, r -> game.notify(l -> l.loginResponse((LoginResponse) r)));
		messageActions.put(StartGame.class, s -> {
			StartGame startGame = (StartGame) s;
			try {
				game.setWorld(World.createClientWorld(startGame.getCreateWorld(), game.getContentPacksContext()));
				game.setMyPlayerId(startGame.getMyPlayerId());
				game.notify(l -> l.startGame());
			} catch (ContentPackException e) {
				Log.error("Error occured when loading contentPack.", e);
				game.notify(l -> l.error(e));
			}
		});
		messageActions.put(ContentPackPart.class,
				p -> game.getContentPackDownloadManager().accept((ContentPackPart) p));
		messageActions.put(RequestContentPacks.class, r -> {
			game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
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
			if (object != null && !(object instanceof EntitiesUpdate)) {
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