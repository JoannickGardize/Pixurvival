package com.pixurvival.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.PlayerInventory;
import com.pixurvival.core.World;
import com.pixurvival.core.map.CompressedChunk;
import com.pixurvival.core.message.ContentPackPart;
import com.pixurvival.core.message.EntitiesUpdate;
import com.pixurvival.core.message.InitializeGame;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.RequestContentPacks;
import com.pixurvival.core.message.TimeResponse;

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
		messageActions.put(RequestContentPacks.class, r -> {
			game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
		});
		messageActions.put(TimeResponse.class, o -> {
			TimeResponse t = (TimeResponse) o;
			long time = (System.currentTimeMillis() - t.getRequesterTime()) / 2;
			game.updatePing(time);
		});
		messageActions.put(PlayerInventory.class, i -> {
			game.getMyInventory().set((PlayerInventory) i);
		});
		messageActions.put(CompressedChunk[].class, c -> {
			World world = game.getWorld();
			if (world != null) {
				for (CompressedChunk compressed : (CompressedChunk[]) c) {
					world.getMap().setChunk(compressed);
				}
			}
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
