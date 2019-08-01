package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.team.TeamMember;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = { "source", "alteration" })
public class PersistentAlterationEntry {
	private TeamMember source;
	private PersistentAlteration alteration;

	private @Setter long termTimeMillis;

	public PersistentAlterationEntry(TeamMember source, PersistentAlteration alteration) {
		this.source = source;
		this.alteration = alteration;
	}
}
