package fr.sharkhendrix.pixurvival.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import fr.sharkhendrix.pixurvival.core.World;
import fr.sharkhendrix.pixurvival.core.message.KryoInitializer;
import fr.sharkhendrix.pixurvival.core.message.LoginRequest;
import fr.sharkhendrix.pixurvival.core.message.LoginResponse;
import fr.sharkhendrix.pixurvival.core.message.PlayerActionRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClientGame {

	private Client client = new Client();
	private ClientListener clientListener;
	private List<ClientGameListener> listeners = new ArrayList<>();
	private @Getter @Setter(AccessLevel.PACKAGE) World world = null;
	private @Getter @Setter(AccessLevel.PACKAGE) long myPlayerId;

	public ClientGame() {
		clientListener = new ClientListener(this);
		client.addListener(clientListener);
		client.start();
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
				client.start();
			}
			client.connect(5000, address, port, port);
			client.sendTCP(new LoginRequest(playerName));
		} catch (IOException e) {
			Log.error("Error when trying to connect to server.", e);
			listeners.forEach(l -> l.loginResponse(LoginResponse.INTERNAL_ERROR));
		}
	}

	public void sendAction(PlayerActionRequest request) {
		client.sendUDP(request);
	}

	public void update(double deltaTimeMillis) {
		if (world != null) {
			clientListener.consumeReceivedObjects();
			world.update(deltaTimeMillis);
		} else {
			clientListener.consumeReceivedObjects();
		}
	}
}
