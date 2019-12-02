package com.pixurvival.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.server.util.ServerMainArgs;

/**
 * Utility class that start the server automotically on port 7777, and start the
 * game when a player connects, usefull for fast local testing.
 * 
 * @author SharkHendrix
 *
 */
public class AutoRunTestServer implements ServerGameListener {
	private ServerGame game;

	public AutoRunTestServer(ServerMainArgs serverArgs) throws IOException {
		game = new ServerGame(serverArgs);
		game.addListener(this);
	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		new Thread(() -> {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			game.startGame(0);
		}).start();
	}

	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		new AutoRunTestServer(ArgsUtils.readArgs(args, ServerMainArgs.class));
	}
}
