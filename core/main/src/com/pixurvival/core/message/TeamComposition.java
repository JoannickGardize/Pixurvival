package com.pixurvival.core.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TeamComposition {

    private String teamName;
    private PlayerInformation[] members;
    private transient Set<Long> playerIds;

    public TeamComposition(String teamName, PlayerInformation[] members) {
        this.teamName = teamName;
        this.members = members;
        playerIds = Arrays.stream(members).map(PlayerInformation::getId).collect(Collectors.toSet());
    }

}
