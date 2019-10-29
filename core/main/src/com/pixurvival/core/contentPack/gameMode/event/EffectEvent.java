package com.pixurvival.core.contentPack.gameMode.event;

import java.util.Collection;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamSet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectEvent extends Event {

	private static final long serialVersionUID = 1L;

	private boolean forEachTeam;

	private EventPosition position;

	/**
	 * The Effect will have as ancestor a virtual TeamMember of the WILD team. The
	 * ancestor's stats are filled with special values :
	 * <ul>
	 * <li>Strength : number of concerned players
	 * <li>Agility : number of repeat (starting to zero)
	 * </ul>
	 */
	private Effect effect;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void perform(World world, int repeatCount) {
		if (forEachTeam) {
			for (Team team : world.getTeamSet()) {
				if (team != TeamSet.WILD_TEAM) {
					perform(world, team.getAliveMembers(), repeatCount);
				}
			}
		} else {
			perform(world, (Collection) world.getEntityPool().get(EntityGroup.PLAYER), repeatCount);
		}
	}

	private void perform(World world, Collection<PlayerEntity> players, int repeatCount) {
		EffectEventTeamMember teamMember = new EffectEventTeamMember(world);
		position.apply(world, players, teamMember.getPosition(), teamMember.getTargetPosition());
		teamMember.getStats().get(StatType.STRENGTH).setBase(players.size());
		teamMember.getStats().get(StatType.AGILITY).setBase(repeatCount);
		EffectEntity effectEntity = new EffectEntity(new OffsetAngleEffect(effect), teamMember);
		world.getEntityPool().add(effectEntity);
	}

}
