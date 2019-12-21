package com.pixurvival.server.lobby;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@RequiredArgsConstructor
public class LobbySessionTeam {
	private @NonNull @Getter @Setter String name;
	private @Getter List<PlayerLobbySession> members = new ArrayList<>();
}
