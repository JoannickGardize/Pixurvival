package com.pixurvival.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.esotericsoftware.minlog.Log;

/**
 * Utility class that start the server automotically on port 7777, and start the
 * game when a player connects, usefull for fast local testing.
 * 
 * @author SharkHendrix
 *
 */
public class AutoRunTestServer implements ServerGameListener {
	private ServerGame game = new ServerGame();

	public AutoRunTestServer() throws IOException {
		game.addListener(this);
		game.startServer(7777);
	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		new Thread(() -> {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			game.startTestGame();
		}).start();
	}

	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (args.length > 0) {
			Log.class.getMethod(args[0]).invoke(null);
		}
		new AutoRunTestServer();
	}
}
