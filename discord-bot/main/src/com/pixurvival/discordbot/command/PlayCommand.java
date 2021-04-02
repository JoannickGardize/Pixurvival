package com.pixurvival.discordbot.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.PlayerEndGameData;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.discordbot.Directories;
import com.pixurvival.discordbot.util.DiscordUtils;
import com.pixurvival.server.GameSession;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.ServerGameListener;
import com.pixurvival.server.lobby.LobbySession;
import com.pixurvival.server.lobby.PlayerLobbySession;
import com.pixurvival.server.util.ServerMainArgs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayCommand extends Command implements ServerGameListener {

	private static final int PORT = 7777;

	private static final long INACTIVE_TRIGGER = TimeUnit.MINUTES.toMillis(15);

	private volatile PixurvivalServer server = null;
	private volatile LobbySession actualLobby = null;
	private volatile long startTime;
	private volatile long inactiveTime = -1;
	private volatile GameSession actualGameSession = null;
	private TextChannel playChannel;

	public PlayCommand() {
		super("play", "", "Start my pixurvival server. If the server is already running, I display its state. If the server is already running and is empty, I restart it.");
		new Thread(() -> {
			try {
				while (true) {
					if (server != null && inactiveTime != -1 && System.currentTimeMillis() - inactiveTime >= INACTIVE_TRIGGER && playChannel != null) {
						stop();
						playChannel.sendMessage("I stopped the server for inactivity.").queue();
					}
					Thread.sleep(5000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void execute(MessageReceivedEvent event, String[] args) {
		if (playChannel == null) {
			List<TextChannel> channels = event.getGuild().getTextChannelsByName(DiscordUtils.PLAY_CHANNEL, true);
			if (!channels.isEmpty()) {
				playChannel = channels.get(0);
			}
		}
		if (!event.getChannel().getName().equals(DiscordUtils.PLAY_CHANNEL)) {
			DiscordUtils.sendMessage(event, "Here <#" + playChannel.getId() + ">");
		}
		if (server != null) {
			if (server.countPlayerConnection() == 0) {
				stop();
				start(true);
			} else {
				displayState();
			}
		} else {
			start(false);
		}
	}

	private void start(boolean isRestart) {
		if (server != null) {
			throw new IllegalStateException();
		}
		ServerMainArgs args = new ServerMainArgs();
		args.setContentPackDirectory(Directories.contentPacks().getPath());
		args.setPort(PORT);
		inactiveTime = System.currentTimeMillis();
		server = new PixurvivalServer(args, this);

		if (isRestart) {
			playChannel.sendMessage("I restarted the server!").queue();
		} else {
			playChannel.sendMessage("I started the server!").queue();
		}

		displayState();
	}

	private void stop() {
		if (server != null) {
			PixurvivalServer oldServer = server;
			server = null;
			oldServer.stopServer();
		}
	}

	private void displayState() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server infos");
		builder.setColor(Color.gray);
		builder.addField("IP", DiscordUtils.getMyIp(), true);
		builder.addField("Port", Integer.toString(PORT), true);
		builder.addField("Version", ReleaseVersion.actual().displayName(), true);
		LobbySession lobby = actualLobby;
		String stateDescription;
		if (lobby != null) {
			List<PlayerLobbySession> players = lobby.getPlayerSessions();
			synchronized (players) {
				stateDescription = "Preparing game, join now! " + (players.isEmpty() ? "The lobby is actually empty."
						: "Players actually in the lobby: " + players.stream().map(p -> p.getLobbyPlayer().getPlayerName()).collect(Collectors.joining(", ")) + ".");
			}
		} else {
			long elapsed = System.currentTimeMillis() - startTime;
			StringBuilder result = new StringBuilder("A game is in progress for ");
			appendTime(result, elapsed);
			result.append(".");
			stateDescription = result.toString();
		}
		builder.addField("State", stateDescription, false);
		builder.setFooter("Running on a Raspberry Pi");

		playChannel.sendMessage(builder.build()).queue();
	}

	private void appendTime(StringBuilder builder, long time) {
		TimeUnit millis = TimeUnit.MILLISECONDS;
		long hours = millis.toHours(time);
		long minutes = millis.toMinutes(time);
		long seconds = millis.toSeconds(time);
		if (hours > 0) {
			builder.append(String.format("%02d", hours)).append(":");
		}
		builder.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));
	}

	private String toDisplayTime(long time) {
		StringBuilder builder = new StringBuilder();
		appendTime(builder, time);
		return builder.toString();
	}

	@Override
	public void lobbyStarted(LobbySession lobbySession) {
		actualLobby = lobbySession;
	}

	@Override
	public void gameStarted(GameSession gameSession) {
		actualGameSession = gameSession;
		startTime = System.currentTimeMillis();
		actualLobby = null;
	}

	@Override
	public void gameEnded(EndGameData data) {
		if (playChannel == null) {
			return;
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Game ended!");
		builder.setColor(Color.gray);
		builder.setDescription("The game has ended! You can join for a new game!");
		builder.addField("Elapsed time", toDisplayTime(data.getTime()), false);
		GameSession gameSession = actualGameSession;
		if (gameSession != null) {
			if (data.getPlayerWonIds().length > 0) {
				builder.addField("Winners", listPlayers(data.getPlayerWonIds(), gameSession), false);
			}
			if (data.getPlayerLostIds().length > 0) {
				builder.addField("Losers", listPlayers(data.getPlayerLostIds(), gameSession), false);
			}
		}
		playChannel.sendMessage(builder.build()).queue();
	}

	private String listPlayers(PlayerEndGameData[] players, GameSession gameSession) {
		return Arrays.stream(players).map(p -> gameSession.sessionOfPlayerId(p.getPlayerId())).filter(Objects::nonNull).map(p -> p.getOriginalPlayer().getName())
				.collect(Collectors.joining(", "));
	}

	@Override
	public void playerLoggedIn(PlayerConnection playerConnection) {
		inactiveTime = -1;
	}

	@Override
	public void playerRejoined(PlayerConnection playerConnection) {
		inactiveTime = -1;
	}

	@Override
	public void disconnected(PlayerConnection playerConnection) {
		if (server.countPlayerConnection() == 0) {
			inactiveTime = System.currentTimeMillis();
		}
	}
}
