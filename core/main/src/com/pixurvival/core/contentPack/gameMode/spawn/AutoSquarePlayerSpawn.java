package com.pixurvival.core.contentPack.gameMode.spawn;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.map.analytics.AreaSearchCriteria;
import com.pixurvival.core.map.analytics.GameAreaConfiguration;
import com.pixurvival.core.map.analytics.MapAnalytics;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoSquarePlayerSpawn implements PlayerSpawn {

	private static final long serialVersionUID = 1L;

	private int size = 490;
	private float minFreeSpace = 0.4f;
	private float maxFreeSpace = 1;

	@Override
	public void apply(World world) {
		TeamSet teamSet = world.getTeamSet();
		AreaSearchCriteria areaSearchCriteria = new AreaSearchCriteria();
		areaSearchCriteria.setNumberOfSpawnSpots(teamSet.getPlayerTeamSize());
		areaSearchCriteria.setSquareSize(size);
		areaSearchCriteria.setMinFreeArea(minFreeSpace);
		areaSearchCriteria.setMaxFreeArea(maxFreeSpace);
		MapAnalytics mapAnalytics = new MapAnalytics(world.getRandom());
		try {
			GameAreaConfiguration config = mapAnalytics.buildGameAreaConfiguration(world.getMap(), areaSearchCriteria);
			world.setSpawnCenter(config.getArea().center());
			int i = 0;
			for (Team team : teamSet) {
				if (team.isPlayerTeam()) {
					Vector2 spawnPosition = config.getSpawnSpots()[i++];
					spawnTeam(team, spawnPosition);
				}
			}
		} catch (MapAnalyticsException e) {
			Log.error("MapAnalyticsException");
		}
	}
}
