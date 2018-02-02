package fr.sharkhendrix.pixurvival.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import fr.sharkhendrix.pixurvival.core.World;
import fr.sharkhendrix.pixurvival.core.network.message.KryoInitializer;
import fr.sharkhendrix.pixurvival.core.network.message.LoginResponse;
import fr.sharkhendrix.pixurvival.core.network.message.PlayerActionRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client();
	private ClientListener clientListener;
	private List<GameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter @Setter(AccessLevel.PACKAGE) long myPlayerId;

	public ClientGame() {
		clientListener = new ClientListener(this);
		client.addListener(clientListener);
		client.start();
		KryoInitializer.apply(client.getKryo());
	}

	public void addListener(GameListener listener) {
		listeners.add(listener);
	}

	void notify(Consumer<GameListener> action) {
		listeners.forEach(action);
	}

	public void connectToServer(String address, int port) {
		try {
			if (client.isConnected()) {
				client.stop();
				client.close();
				client.start();
			}
			client.connect(5000, address, port, port);

		} catch (IOException e) {
			Log.error("Error when trying to connect to server.", e);
			listeners.forEach(l -> l.loginResponse(LoginResponse.INTERNAL_ERROR));
		}
	}

	public void sendAction(PlayerActionRequest request) {
		client.sendUDP(request);
	}

	public void updateWorld(double deltaTimeMillis) {
		if (world != null) {
			synchronized (world) {
				clientListener.applyReceivedObjects();
				world.update(deltaTimeMillis);
			}
		}
	}
}
