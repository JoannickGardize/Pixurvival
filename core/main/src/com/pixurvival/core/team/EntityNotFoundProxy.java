package com.pixurvival.core.team;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.EntityGroup;

import lombok.Getter;
import lombok.NonNull;

public class EntityNotFoundProxy extends FlatTeamMember {

	private @Getter EntityGroup group;
	private @Getter long id;
	private TeamMember member = null;

	public EntityNotFoundProxy(@NonNull World world, EntityGroup group, long id) {
		super(world);
		this.group = group;
		this.id = id;
	}

	@Override
	public TeamMember findIfNotFound() {
		if (member != null) {
			return member;
		}
		member = (TeamMember) getWorld().getEntityPool().get(group, id);
		if (member == null) {
			return this;
		} else {
			return member;
		}
	}
}
