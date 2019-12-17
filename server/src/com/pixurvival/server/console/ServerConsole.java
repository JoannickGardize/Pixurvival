package com.pixurvival.server.console;

import java.util.Scanner;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.ServerGameListener;
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
		}));
	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		Log.info("New player connected : " + playerConnection);
	}

	@Override
	public void run() {
		Scanner reader = new Scanner(System.in);
		while (running) {
			System.out.print(" -> ");
			String[] args = CommandArgsUtils.splitArgs(reader.nextLine());
			try {
				rootCommandProcessor.process(args);
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
}
