package com.pixurvival.server.lobby;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LobbySessionTeam {
    private @NonNull
    @Getter
    @Setter String name;
    private @Getter List<PlayerLobbySession> members = new ArrayList<>();
}
