package com.pixurvival.core.message.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeTeamRequest implements LobbyRequest {
    private String teamName;
}
