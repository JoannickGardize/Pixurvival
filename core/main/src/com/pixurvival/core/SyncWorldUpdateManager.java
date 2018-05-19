package com.pixurvival.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SyncWorldUpdateManager {

	public static final int HISTORY_SIZE = 16;

	private @NonNull World world;
	private List<SyncWorldUpdate> waitingList = new ArrayList<>();
	private NavigableMap<Long, SyncWorldUpdate> history = new TreeMap<>();

	public void add(SyncWorldUpdate syncWorldUpdate) {
		waitingList.add(syncWorldUpdate);
	}

	public void update() {
		waitingList.sort((u1, u2) -> (int) (u1.getUpdateId() - u2.getUpdateId()));
		for (int i = waitingList.size() - 1; i >= 0; i--) {
			SyncWorldUpdate syncWorldUpdate = waitingList.get(i);
			history.put(syncWorldUpdate.getUpdateId(), syncWorldUpdate);
			history.tailMap(syncWorldUpdate.getUpdateId(), true).values().forEach(u -> {
				if (syncWorldUpdate.getCompressedChunks() != null) {
					world.getMap().addAllChunks(syncWorldUpdate.getCompressedChunks());
				}
				if (syncWorldUpdate.getPlayerData() != null) {
					world.addPlayerData(syncWorldUpdate.getPlayerData());
				}
				if (syncWorldUpdate.getStructureUpdates() != null) {
					world.getMap().applyUpdate(syncWorldUpdate.getStructureUpdates());
				}
			});
		}
		waitingList.clear();
		while (history.size() > HISTORY_SIZE) {
			history.pollFirstEntry();
		}
	}

}
