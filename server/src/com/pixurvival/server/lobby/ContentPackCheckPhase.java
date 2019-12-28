package com.pixurvival.server.lobby;

import java.util.HashSet;
import java.util.Set;

import com.pixurvival.core.message.ContentPackCheck;
import com.pixurvival.core.message.lobby.ContentPackReady;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.RefuseContentPack;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ContentPackCheckPhase implements LobbyPhase {

	private Set<PlayerLobbySession> readySessions = new HashSet<>();

	private @NonNull LobbySession session;

	private @Setter StartingGameData data;

	@Override
	public void started() {
		readySessions.clear();
		ContentPackCheck check = new ContentPackCheck(data.getContentPack().getIdentifier(), session.getServer().getContentPackSerialization().getChecksum(data.getContentPack().getIdentifier()));
		session.getPlayerSessions().forEach(p -> p.getConnection().sendTCP(check));
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
		if (lobbyMessage instanceof ContentPackReady) {
			if (data.getContentPack().getIdentifier().equals(((ContentPackReady) lobbyMessage).getIdentifier())) {
				readySessions.add(playerSession);
				if (readySessions.size() == session.getPlayerSessions().size()) {
					session.getPhase(StartingGamePhase.class).setData(data);
					session.setCurrentPhase(StartingGamePhase.class);
				}
			}
		} else if (lobbyMessage instanceof RefuseContentPack && data.getContentPack().getIdentifier().equals(((RefuseContentPack) lobbyMessage).getIdentifier())) {
			session.setCurrentPhase(PreparingPhase.class).abordStartingGame();
		}
	}

}
