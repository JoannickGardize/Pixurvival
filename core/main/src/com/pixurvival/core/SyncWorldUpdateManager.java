package com.pixurvival.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.LogUtils;
import com.pixurvival.core.util.ObjectPools;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SyncWorldUpdateManager {

	public static final int HISTORY_SIZE = 16;

	private @NonNull World world;
	private List<WorldUpdate> waitingList = new ArrayList<>();
	private NavigableMap<Long, WorldUpdate> history = new TreeMap<>();

	public void add(WorldUpdate syncWorldUpdate) {
		waitingList.add(syncWorldUpdate);
	}

	public void update() {
		waitingList.sort((u1, u2) -> (int) (u1.getUpdateId() - u2.getUpdateId()));
		for (int i = waitingList.size() - 1; i >= 0; i--) {
			WorldUpdate worldUpdate = waitingList.get(i);
			history.put(worldUpdate.getUpdateId(), worldUpdate);
			history.tailMap(worldUpdate.getUpdateId(), true).values().forEach(u -> {
				LogUtils.debug("Applying WorldUpdate  [", worldUpdate.getUpdateId(), "]");
				if (worldUpdate.getEntityUpdateLength() > 0) {
					worldUpdate.getEntityUpdateByteBuffer().position(0);
					world.getEntityPool().applyUpdate(worldUpdate.getEntityUpdateByteBuffer());
				}
				world.getMap().addAllChunks(worldUpdate.getCompressedChunks());
				world.addPlayerData(worldUpdate.getPlayerData());
				world.getMap().applyUpdate(worldUpdate.getStructureUpdates());
			});
		}
		waitingList.clear();
		while (history.size() > HISTORY_SIZE) {
			ObjectPools.getWorldUpdatePool().offer(history.pollFirstEntry().getValue());
		}
	}

}
