package com.pixurvival.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.ContentPacksContext;
import com.pixurvival.core.message.ClientReady;
import com.pixurvival.core.message.KryoInitializer;
import com.pixurvival.core.message.LoginRequest;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.PlayerActionRequest;
import com.pixurvival.core.message.RequestContentPacks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client();
	private ClientListener clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter @Setter(AccessLevel.PACKAGE) long myPlayerId;
	private @Getter ContentPackDownloadManager contentPackDownloadManager = new ContentPackDownloadManager();
	private @Getter ContentPacksContext contentPacksContext = new ContentPacksContext("contentPacks");

	public ClientGame() {
		Log.set(Log.LEVEL_DEBUG);
		KryoInitializer.apply(client.getKryo());
	}

	public void addListener(ClientGameListener listener) {
		listeners.add(listener);
	}

	void notify(Consumer<ClientGameListener> action) {
		listeners.forEach(action);
	}

	public void connectToServer(String address, int port, String playerName) {
		try {
			if (client.isConnected()) {
				client.stop();
				client.close();
			}
			clientListener = new ClientListener(this);
			client.addListener(clientListener);
			client.start();
			client.connect(5000, address, port, port);
			client.sendTCP(new LoginRequest(playerName));
		} catch (Exception e) {
			Log.error("Error when trying to connect to server.", e);
			listeners.forEach(l -> l.loginResponse(LoginResponse.INTERNAL_ERROR));
		}
	}

	public void startLocalGame() {
		// TODO local game
	}

	public void sendAction(PlayerActionRequest request) {
		if (world != null) {
			if (world.getType() == World.Type.CLIENT) {
				client.sendUDP(request);
			} else {
				// TODO local game
			}
		}
	}

	public void update(double deltaTimeMillis) {
		if (world != null) {
			clientListener.consumeReceivedObjects();
			world.update(deltaTimeMillis);
		} else {
			clientListener.consumeReceivedObjects();
		}
	}

	public void checkMissingPacks(ContentPackIdentifier[] identifiers) {
		Collection<ContentPackIdentifier> list = contentPacksContext.list();
		List<ContentPackIdentifier> missingPacks = new ArrayList<>();
		for (ContentPackIdentifier identifier : identifiers) {
			if (!list.contains(identifier)) {
				missingPacks.add(identifier);
			}
		}
		if (!missingPacks.isEmpty()) {
			client.sendTCP(
					new RequestContentPacks(missingPacks.toArray(new ContentPackIdentifier[missingPacks.size()])));
		}
	}

	public void notifyReady() {
		client.sendTCP(new ClientReady());
	}
}
