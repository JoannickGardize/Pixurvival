package com.pixurvival.core.team;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * A team can be made of any {@link LivingEntity}, but this Team class only
 * stores {@link PlayerEntity}{@code s}, because this is the only ones needed.
 * 
 * @author SharkHendrix
 *
 */
@Value
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Team implements Iterable<PlayerEntity> {

	private int id;
	private String name;
	private boolean playerTeam;

	private Set<PlayerEntity> aliveMembers = new HashSet<>();
	private Set<PlayerEntity> deadMembers = new HashSet<>();

	public Team(int id, String name) {
		this(id, name, true);
	}

	public void addAlive(PlayerEntity playerEntity) {
		aliveMembers.add(playerEntity);
		deadMembers.remove(playerEntity);
	}

	public void addDead(PlayerEntity playerEntity) {
		aliveMembers.remove(playerEntity);
		deadMembers.add(playerEntity);
	}

	public void remove(PlayerEntity playerEntity) {
		aliveMembers.remove(playerEntity);
		deadMembers.remove(playerEntity);
	}

	public int aliveMemberCount() {
		return aliveMembers.size();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Iterator<PlayerEntity> iterator() {
		return aliveMembers.iterator();
	}

	public int totalSize() {
		return aliveMembers.size() + deadMembers.size();
	}
}
