package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamSet;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class EffectEvent extends Event {

    private static final long serialVersionUID = 1L;

    private boolean forEachTeam;

    @Valid
    private EventPosition position;

    /**
     * Maximum repeat value for the effect's stat formulas. 0 for no limit.
     */
    @Positive
    private int maximumRepeatValue;

    /**
     * The Effect will have as ancestor a virtual TeamMember of the WILD team. The
     * ancestor's stats are filled with special values :
     * <ul>
     * <li>Strength : number of concerned players
     * <li>Agility : number of repeat (starting to zero)
     * <li>Intelligence : number of concerned players * number of repeat
     * </ul>
     * TODO remove this when obsolete
     */
    @ElementReference
    private Effect effect;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void perform(World world, int repeatCount) {
        int consideredRepeatCount = maximumRepeatValue > 0 ? Math.min(repeatCount, maximumRepeatValue) : repeatCount;

        if (forEachTeam) {
            for (Team team : world.getTeamSet()) {
                if (team != TeamSet.WILD_TEAM) {
                    perform(world, team.getAliveMembers(), team.totalSize(), consideredRepeatCount);
                }
            }
        } else {
            Collection players = world.getEntityPool().get(EntityGroup.PLAYER);
            perform(world, players, players.size(), consideredRepeatCount);
        }
    }

    private void perform(World world, Collection<PlayerEntity> players, int numberOfPlayers, int repeatCount) {
        EffectEventTeamMember teamMember = new EffectEventTeamMember(world);
        position.apply(world, players, teamMember.getPosition(), teamMember.getTargetPosition());
        teamMember.getStats().get(StatType.STRENGTH).setBase(numberOfPlayers);
        teamMember.getStats().get(StatType.AGILITY).setBase(repeatCount);
        teamMember.getStats().get(StatType.INTELLIGENCE).setBase(numberOfPlayers * repeatCount);
        EffectEntity effectEntity = new EffectEntity(new OffsetAngleEffect(effect), teamMember);
        world.getEntityPool().addNew(effectEntity);
    }

}
