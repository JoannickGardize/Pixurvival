package com.pixurvival.core.livingEntity;

import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "id")
public class Team {

	private int id;
	private String name;

	private Set<PlayerEntity> aliveMembers = new HashSet<>();
	private Set<PlayerEntity> deadMembers = new HashSet<>();

	public Team(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public void addAlive(PlayerEntity playerEntity) {
		aliveMembers.add(playerEntity);
		deadMembers.remove(playerEntity);
	}

	public void addDead(PlayerEntity playerEntity) {
		aliveMembers.remove(playerEntity);
		deadMembers.add(playerEntity);
	}

	public int aliveMemberCount() {
		return aliveMembers.size();
	}

	@Override
	public String toString() {
		return name;
	}
}
