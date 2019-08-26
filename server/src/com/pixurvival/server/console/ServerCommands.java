package com.pixurvival.server.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGame;

public class ServerCommands extends CommandMultiplexer {

	public ServerCommands(ServerGame game) {
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

		addProcessor("bind", new SimpleCommandProcessor(1, args -> game.startServer(Integer.parseInt(args[0]))));
		addProcessor("start", new SimpleCommandProcessor(1, args -> game.startGame(Integer.parseInt(args[0]))));
		addProcessor("team", teamCommandMultiplexer);
	}
}
