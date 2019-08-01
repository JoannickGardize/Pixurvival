package com.pixurvival.core.team;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.esotericsoftware.kryo.util.IntMap;

public class TeamSet implements Iterable<Team> {

	/**
	 * The Wild team represents all creatures excepts the ones owned by players.
	 */
	public static final Team WILD_TEAM = new Team(-1, "Wild");

	private IntMap<Team> teams = new IntMap<>();
	private Map<String, Team> teamsByName = new HashMap<>();
	private int nextId = 0;

	public TeamSet() {
		teams.put(WILD_TEAM.getId(), WILD_TEAM);
		teamsByName.put(WILD_TEAM.getName(), WILD_TEAM);
	}

	public Team createTeam(String name) {
		if (teamsByName.containsKey(name)) {
			throw new IllegalArgumentException("The team name " + name + " already exists");
		}
		Team team = new Team(nextId++, name);
		teams.put(team.getId(), team);
		teamsByName.put(name, team);
		return team;
	}

	public Team get(int id) {
		return teams.get(id);
	}

	public Team get(String name) {
		return teamsByName.get(name);
	}

	/**
	 * @return The number of teams, without the special "Wild" team.
	 */
	public int size() {
		return teamsByName.size() - 1;
	}

	public String[] getNames() {
		String[] names = new String[teams.size];
		for (Team team : teams.values()) {
			if (team.getId() >= 0) {
				names[team.getId()] = team.getName();
			}
		}
		return names;
	}

	@Override
	public Iterator<Team> iterator() {
		return teamsByName.values().iterator();
	}
}
