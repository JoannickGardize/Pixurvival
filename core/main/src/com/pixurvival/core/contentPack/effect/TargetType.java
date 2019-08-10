package com.pixurvival.core.contentPack.effect;

import java.util.function.BiPredicate;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TargetType {
	ALL_ENEMIES((self, other) -> self.getTeam() != other.getTeam()),
	ALL_ALLIES((self, other) -> self.getTeam() == other.getTeam()),
	OTHER_ALLIES((self, other) -> self.getTeam() == other.getTeam() && self != other);

	private @Getter BiPredicate<TeamMember, LivingEntity> test;
}
