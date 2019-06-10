package com.pixurvival.server.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.CommonMainArgs;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGame;
import com.pixurvival.server.ServerGameListener;

public class ServerConsole implements Runnable, ServerGameListener {

	private boolean running = true;
	private ServerGame game;
	private CommandMultiplexer rootCommandProcessor = new CommandMultiplexer();

	public ServerConsole(CommonMainArgs mainArgs) {
		game = new ServerGame(mainArgs);
		game.addListener(this);

		CommandMultiplexer teamCommandMultiplexer = new CommandMultiplexer();
		teamCommandMultiplexer.addProcessor("set", new SimpleCommandProcessor(2, args -> {
			PlayerConnection player = game.getPlayerConnection(args[0]);
			if (player == null) {
				Log.warn("No player with name " + args[0]);
			} else {
				player.setRequestedTeamName(args[1]);
			}
		}));
		teamCommandMultiplexer.addProcessor("recap", new SimpleCommandProcessor(0, args -> {
			Map<String, List<String>> teamMap = new HashMap<>();
			game.foreachPlayers(p -> teamMap.computeIfAbsent(p.getRequestedTeamName(), name -> new ArrayList<>()).add(p.toString()));
			for (Entry<String, List<String>> entry : teamMap.entrySet()) {
				System.out.print(entry.getKey());
				System.out.print(" -> [");
				String separator = "";
				for (String name : entry.getValue()) {
					System.out.print(separator);
					System.out.print(name);
					separator = ", ";
				}
				System.out.println("]");
			}
		}));

		rootCommandProcessor.addProcessor("bind", new SimpleCommandProcessor(1, args -> game.startServer(Integer.parseInt(args[0]))));
		rootCommandProcessor.addProcessor("start", new SimpleCommandProcessor(1, args -> game.startGame(Integer.parseInt(args[0]))));
		rootCommandProcessor.addProcessor("exit", new SimpleCommandProcessor(1, args -> {
			game.stopServer();
			running = false;
		}));
		rootCommandProcessor.addProcessor("team", teamCommandMultiplexer);
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
		new ServerConsole(ArgsUtils.readArgs(args, CommonMainArgs.class)).run();
		System.exit(0);
	}
}
