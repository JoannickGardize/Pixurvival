package com.pixurvival.core.livingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TeamSet implements Iterable<Team> {

	/**
	 * The Wild team represents all creatures excepts the ones owned by players.
	 */
	public static final Team WILD_TEAM = new Team((short) -1, "Wild");

	private Map<Short, Team> teams = new HashMap<>();

	private short nextId = 0;

	public TeamSet() {
		teams.put(WILD_TEAM.getId(), WILD_TEAM);
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
