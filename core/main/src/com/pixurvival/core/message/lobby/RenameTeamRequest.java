package com.pixurvival.core.message.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RenameTeamRequest implements LobbyRequest {

    private String oldName;
    private String newName;
}
