package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiPredicate;

@AllArgsConstructor
public enum TargetType {
    ALL_ENEMIES((self, other) -> self.getTeam() != other.getTeam()),
    ALL_ALLIES((self, other) -> self.getTeam() == other.getTeam()),
    OTHER_ALLIES((self, other) -> self.getTeam() == other.getTeam() && self.getOrigin() != other),
    ORIGIN((self, other) -> other == self.getOrigin()),
    ALL_OTHERS((self, other) -> self.getOrigin() != other),
    STRUCTURES((self, other) -> false); // Fake target, special usage

    private @Getter BiPredicate<TeamMember, LivingEntity> test;
}
