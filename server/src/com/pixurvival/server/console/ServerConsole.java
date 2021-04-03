package com.pixurvival.server.console;

import java.util.Scanner;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.server.GameSession;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGameListener;
import com.pixurvival.server.lobby.LobbySession;
import com.pixurvival.server.util.ServerMainArgs;

public class ServerConsole implements Runnable, ServerGameListener {

	private boolean running = true;
	private PixurvivalServer game;
	private CommandMultiplexer rootCommandProcessor;

	public ServerConsole(ServerMainArgs mainArgs) {
		game = new PixurvivalServer(mainArgs);
		game.addListener(this);

		rootCommandProcessor = new ServerCommands(game);

		rootCommandProcessor.addProcessor("exit", new SimpleCommandProcessor(1, args -> {
			game.stopServer();
			running = false;
			return true;
		}));
	}

	@Override
	public void playerRejoined(PlayerConnection playerConnection) {
		Log.info("New player connected : " + playerConnection);
	}

	@Override
	public void run() {
		Scanner reader = new Scanner(System.in);
		while (running) {
			System.out.print(" -> ");
			String line = reader.nextLine();
			String[] args = CommandArgsUtils.splitArgs(line);
			try {
				if (!rootCommandProcessor.process(args)) {
					game.sendCommand(line);
				}
			} catch (Exception e) {
				Log.error("Command execution fail", e);
			}
		}
		reader.close();
	}

	public static void main(String[] args) {
		new ServerConsole(ArgsUtils.readArgs(args, ServerMainArgs.class)).run();
		System.exit(0);
	}

	@Override
	public void lobbyStarted(LobbySession lobbySession) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameStarted(GameSession gameSession) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameEnded(EndGameData data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(PlayerConnection playerConnection) {
		// TODO Auto-generated method stub

	}
}
