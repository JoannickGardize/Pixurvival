package com.pixurvival.core.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

public class TeleportationAlteration extends Alteration {

    private static final long serialVersionUID = 1L;

    @Override
    public void targetedApply(TeamMember source, TeamMember entity) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).teleport(source.getPosition());
        }
    }

}
