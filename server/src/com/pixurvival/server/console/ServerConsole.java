package com.pixurvival.server.console;

import java.io.IOException;
import java.util.Scanner;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGame;
import com.pixurvival.server.ServerGameListener;

public class ServerConsole implements Runnable, ServerGameListener {

	private boolean running = true;
	private ServerGame game = new ServerGame();

	public ServerConsole() {
		game.addListener(this);
	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		System.out.println("New player connected : " + playerConnection);
	}

	@Override
	public void run() {
		Scanner reader = new Scanner(System.in);
		while (running) {
			System.out.print("-> ");
			CommandInput commandInput = new CommandInput(reader.nextLine());
			switch (commandInput.getName()) {
			case "bind":
				if (commandInput.argsLength() == 1) {
					try {
						game.startServer(Integer.valueOf(commandInput.getArg(0)));
					} catch (NumberFormatException | IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case "start":
				game.startTestGame();
				break;
			case "ls-pack":
				for (ContentPackIdentifier identifier : game.getContentPacksContext().list()) {
					System.out.println(identifier);
				}
				break;
			case "exit":
				game.stopServer();
				running = false;
			}

		}
		reader.close();
	}

	public static void main(String[] args) {
		new ServerConsole().run();
	}

}