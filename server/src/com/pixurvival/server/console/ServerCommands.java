package com.pixurvival.server.console;

import com.pixurvival.server.PixurvivalServer;

public class ServerCommands extends CommandMultiplexer {

    public ServerCommands(PixurvivalServer game) {
        // CommandMultiplexer teamCommandMultiplexer = new CommandMultiplexer();
        // addProcessor("bind", new SimpleCommandProcessor(1, args ->
        // game.startServer(Integer.parseInt(args[0]))));
        // addProcessor("team", teamCommandMultiplexer);
        // addProcessor("op", new SimpleCommandProcessor(1, args -> {
        // PlayerConnection connection = game.getPlayerConnection(args[0]);
        // if (connection != null && connection.getPlayerEntity() != null) {
        // connection.getPlayerEntity().setOperator(true);
        // System.out.println("Opped " + connection);
        // }
        // }));
    }
}
