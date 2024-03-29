package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingElementAlteration extends UniqueAlteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private FollowingElement followingElement;

    @Override
    public void uniqueApply(TeamMember source, TeamMember entity) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).prepareTargetedAlteration();
        }
        followingElement.apply(entity);
    }
}