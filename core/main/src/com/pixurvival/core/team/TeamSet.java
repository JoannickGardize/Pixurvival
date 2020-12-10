package com.pixurvival.core.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Contains player teams of a party
 * 
 * @author SharkHendrix
 *
 */
public class TeamSet implements Iterable<Team> {

	/**
	 * The Wild team represents all creatures excepts the ones owned by players.
	 */
	public static final Team WILD_TEAM = new Team(0, "Wild", false);

	private List<Team> teams = new ArrayList<>();
	private Map<String, Team> teamsByName = new HashMap<>();
	private @Getter int playerTeamSize = 0;

	public TeamSet() {
		teams.add(WILD_TEAM);
		teamsByName.put(WILD_TEAM.getName(), WILD_TEAM);
	}

	public Team createTeam(String name) {
		if (teamsByName.containsKey(name)) {
			throw new IllegalArgumentException("The team name " + name + " already exists");
		}
		Team team = new Team(teams.size(), name);
		teams.add(team);
		teamsByName.put(name, team);
		playerTeamSize++;
		return team;
	}

	public Team get(int id) {
		return teams.get(id);
	}

	public Team get(String name) {
		return teamsByName.get(name);
	}

	/**
	 * @return The number of player team.
	 */
	public int size() {
		return teamsByName.size();
	}

	public String[] getNames() {
		String[] names = new String[teams.size() - 1];
		for (int i = 1; i < teams.size(); i++) {
			Team team = teams.get(i);
			names[team.getId()] = team.getName();
		}
		return names;
	}

	@Override
	public Iterator<Team> iterator() {
		return teamsByName.values().iterator();
	}
}
