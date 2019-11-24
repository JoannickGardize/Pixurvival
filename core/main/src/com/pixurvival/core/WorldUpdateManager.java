package com.pixurvival.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Plugin;

import lombok.RequiredArgsConstructor;

/**
 * This class simulate reliability of received {@link WorldUpdate}s, because of
 * UDP protocol. <br>
 * TODO request lost paquets
 * 
 * @author SharkHendrix
 *
 */
@RequiredArgsConstructor
public class WorldUpdateManager implements Plugin<World> {

	public static final int HISTORY_SIZE = 32;

	private List<WorldUpdate> waitingList = new ArrayList<>();
	private NavigableMap<Long, WorldUpdate> history = new TreeMap<>();

	public void offer(WorldUpdate worldUpdate) {
		waitingList.add(worldUpdate);
	}

	@Override
	public void update(World world) {
		waitingList.sort((u1, u2) -> (int) (u1.getUpdateId() - u2.getUpdateId()));
		long smallestId = Long.MAX_VALUE;
		for (int i = 0; i < waitingList.size(); i++) {
			WorldUpdate worldUpdate = waitingList.get(i);
			history.put(worldUpdate.getUpdateId(), worldUpdate);
			if (worldUpdate.getUpdateId() < smallestId) {
				smallestId = worldUpdate.getUpdateId();
			}
		}
		history.tailMap(smallestId).values().forEach(u -> {
			if (u.getEntityUpdateLength() > 0) {
				u.getEntityUpdateByteBuffer().position(0);
				world.getEntityPool().applyUpdate(u.getEntityUpdateByteBuffer());
			}
			world.getMap().addAllChunks(u.getCompressedChunks());
			world.getMap().applyUpdate(u.getStructureUpdates());
		});

		waitingList.clear();
		while (history.size() > HISTORY_SIZE) {
			ObjectPools.getWorldUpdatePool().offer(history.pollFirstEntry().getValue());
		}
	}
}