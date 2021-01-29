package com.pixurvival.core.alteration;

import java.util.function.BiFunction;

import com.pixurvival.core.team.TeamMember;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AlterationTarget {
	ORIGIN((s, t) -> s.getOrigin()),
	SELF((s, t) -> s),
	TARGET((s, t) -> t);

	private @Getter BiFunction<TeamMember, TeamMember, TeamMember> function;
}
