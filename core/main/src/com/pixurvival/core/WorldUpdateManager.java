package com.pixurvival.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.LogUtils;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Plugin;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldUpdateManager implements Plugin<World> {

	public static final int HISTORY_SIZE = 16;

	private List<WorldUpdate> waitingList = new ArrayList<>();
	private NavigableMap<Long, WorldUpdate> history = new TreeMap<>();
	private List<PlayerData> playerDataList = new ArrayList<>();

	public void offer(WorldUpdate syncWorldUpdate) {
		waitingList.add(syncWorldUpdate);
	}

	public void offer(PlayerData[] playerDataArray) {
		for (PlayerData playerData : playerDataArray) {
			playerDataList.add(playerData);
		}
	}

	@Override
	public void update(World world) {
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
				playerDataList.addAll(worldUpdate.getPlayerData());
				world.getMap().applyUpdate(worldUpdate.getStructureUpdates());
			});
		}
		waitingList.clear();
		while (history.size() > HISTORY_SIZE) {
			ObjectPools.getWorldUpdatePool().offer(history.pollFirstEntry().getValue());
		}
		if (!playerDataList.isEmpty()) {
			for (int i = 0; i < playerDataList.size(); i++) {
				PlayerData playerData = playerDataList.get(i);
				PlayerEntity e = (PlayerEntity) world.getEntityPool().get(EntityGroup.PLAYER, playerData.getId());
				if (e != null) {
					e.applyData(playerData);
					playerDataList.remove(i);
					i--;
				}
			}
		}
	}

}
