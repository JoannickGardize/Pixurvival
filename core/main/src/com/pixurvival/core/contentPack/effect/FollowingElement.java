package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class FollowingElement implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract void apply(TeamMember origin);
}