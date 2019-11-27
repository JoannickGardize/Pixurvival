package com.pixurvival.client;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Plugin;

/**
 * This is in charge to treat {@link WorldUpdate}s, because of UDP
 * unreliability, it keeps an history of the WorldUpdates to retroactively
 * re-apply unordered updates.
 * 
 * @author SharkHendrix
 *
 */
public class WorldUpdateManager implements Plugin<World> {

	public static final int HISTORY_SIZE = 32;

	private List<Long> ackList = new ArrayList<>();
	private List<Long> resendAckList = new ArrayList<>();
	private List<WorldUpdate> waitingList = new ArrayList<>();
	private NavigableMap<Long, WorldUpdate> history = new TreeMap<>();
	private long previousSendTime = 0;
	private ClientGame client;

	public WorldUpdateManager(ClientGame client) {
		this.client = client;
	}

	public void offer(WorldUpdate worldUpdate) {
		waitingList.add(worldUpdate);
		ackList.add(worldUpdate.getUpdateId());
	}

	@Override
	public void update(World world) {
		handeWorldUpdates(world);
		handleClientStream(world);
	}

	private void handeWorldUpdates(World world) {
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

	private void handleClientStream(World world) {
		long time = world.getTime().getTimeMillis();
		if (time - previousSendTime >= GameConstants.CLIENT_STREAM_INTERVAL) {
			previousSendTime = time;
			ClientStream clientStream = new ClientStream();
			clientStream.setTime(world.getTime().getTimeMillis());
			clientStream.setTargetPosition(client.getMyPlayer().getTargetPosition());
			long[] acks = new long[ackList.size() + resendAckList.size()];
			for (int i = 0; i < ackList.size(); i++) {
				acks[i] = ackList.get(i);
			}
			for (int i = 0; i < resendAckList.size(); i++) {
				acks[i + ackList.size()] = resendAckList.get(i);
			}
			clientStream.setAcks(acks);
			client.send(clientStream);
			resendAckList.clear();
			List<Long> tmpSwap = ackList;
			ackList = resendAckList;
			resendAckList = tmpSwap;
		}
	}
}