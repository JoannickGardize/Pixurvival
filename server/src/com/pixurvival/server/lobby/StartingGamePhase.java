package com.pixurvival.server.lobby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.PlayerInformation;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.team.Team;
import com.pixurvival.server.GameSession;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class StartingGamePhase implements LobbyPhase {

	private @NonNull LobbySession session;
	private @Setter StartingGameData data;
	private Set<PlayerLobbySession> readySessions = new HashSet<>();
	private GameSession waitingGameSession;

	@Override
	public void started() {
		ContentPack contentPack = data.getContentPack();
		World world = World.createServerWorld(contentPack, data.getGameModeId());
		waitingGameSession = new GameSession(world);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setContentPackIdentifier(contentPack.getIdentifier());
		createWorld.setGameModeId(data.getGameModeId());
		List<TeamComposition> teamCompositionList = new ArrayList<>();
		for (int i = 0; i < session.getTeams().size(); i++) {
			LobbySessionTeam lobbyTeam = session.getTeams().get(i);
			List<PlayerLobbySession> members = lobbyTeam.getMembers();
			if (members.isEmpty()) {
				continue;
			}
			PlayerInformation[] playerInformations = new PlayerInformation[members.size()];
			Team team = world.getTeamSet().createTeam(lobbyTeam.getName());
			for (int j = 0; j < members.size(); j++) {
				PlayerLobbySession p = members.get(j);
				PlayerEntity playerEntity = new PlayerEntity();
				world.getEntityPool().add(playerEntity);
				world.getPlayerEntities().put(playerEntity.getId(), playerEntity);
				world.getEntityPool().flushNewEntities();
				playerEntity.setTeam(team);
				playerEntity.setName(p.getConnection().toString());
				waitingGameSession.createPlayerSession(p.getConnection(), playerEntity);
				playerInformations[j] = new PlayerInformation(playerEntity.getId(), playerEntity.getName());
			}
			teamCompositionList.add(new TeamComposition(team.getName(), playerInformations));
		}
		TeamComposition[] teamCompositions = teamCompositionList.toArray(new TeamComposition[teamCompositionList.size()]);

		waitingGameSession.setTeamCompositions(teamCompositions);
		createWorld.setTeamCompositions(teamCompositions);

		world.initializeGame();

		waitingGameSession.foreachPlayers(playerSession -> {
			PlayerEntity playerEntity = playerSession.getPlayerEntity();
			createWorld.setMyPlayerId(playerEntity.getId());
			createWorld.setMyTeamId(playerEntity.getTeam().getId());
			createWorld.setMyPosition(playerEntity.getPosition());
			createWorld.setInventory(playerEntity.getInventory());
			playerSession.getConnection().sendTCP(createWorld);
		});
	}

	@Override
	public void playerEntered(PlayerLobbySession playerSession) {
		playerSession.getConnection().close();
	}

	@Override
	public void playerLeaved(PlayerLobbySession playerSession) {
		session.setCurrentPhase(PreparingPhase.class);
		session.getCurrentPhase().playerLeaved(playerSession);
	}

	@Override
	public void received(PlayerLobbySession playerSession, LobbyMessage lobbyMessage) {
		if (lobbyMessage instanceof GameReady) {
			readySessions.add(playerSession);
			if (readySessions.size() == session.getPlayerSessions().size()) {
				session.terminate();

				waitingGameSession.foreachPlayers(s -> s.getConnection().sendTCP(new StartGame(0, waitingGameSession.getWorld().getSpawnCenter())));
				session.getServer().runGame(waitingGameSession);
				session.getServer().addListener(waitingGameSession);
			}

		}

	}
}
