package com.pixurvival.core.livingEntity;

import java.util.Iterator;

import com.esotericsoftware.kryo.util.IntMap;

public class TeamSet implements Iterable<Team> {

	/**
	 * The Wild team represents all creatures excepts the ones owned by players.
	 */
	public static final Team WILD_TEAM = new Team(-1, "Wild");

	/**
	 * The team that contains all players for solo games or cooperatives games;
	 */
	public static final Team PLAYERS_TEAM = new Team(-2, "Wild");

	private IntMap<Team> teams = new IntMap<>();

	private short nextId = 0;

	public TeamSet() {
		teams.put(WILD_TEAM.getId(), WILD_TEAM);
		teams.put(PLAYERS_TEAM.getId(), PLAYERS_TEAM);
	}

	public Team createTeam(String name) {
		Team team = new Team(nextId++, name);
		teams.put(team.getId(), team);
		return team;
	}

	public Team get(short id) {
		return teams.get(id);
	}

	@Override
	public Iterator<Team> iterator() {
		return teams.values().iterator();
	}
}
