package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatAlteration extends PersistentAlteration {

    private static final long serialVersionUID = 1L;

    @Valid
    private StatModifier statModifier = new StatModifier();

    @Override
    public Object begin(TeamMember source, LivingEntity entity) {
        entity.getStats().addModifier(statModifier);
        return null;
    }

    @Override
    public void restore(TeamMember source, LivingEntity target, Object data) {
        target.getStats().addModifier(statModifier);
    }

    @Override
    public void end(TeamMember source, LivingEntity entity, Object data) {
        entity.getStats().removeModifier(statModifier);
    }
}
