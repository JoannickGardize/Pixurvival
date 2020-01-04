package com.pixurvival.client;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.CooldownAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Plugin;
import com.pixurvival.core.util.Vector2;

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
	private PixurvivalClient client;

	public WorldUpdateManager(PixurvivalClient client) {
		this.client = client;
	}

	public void offer(WorldUpdate worldUpdate) {
		waitingList.add(worldUpdate);
		ackList.add(worldUpdate.getUpdateId());
	}

	@Override
	public void update(World world) {
		handleWorldUpdates(world);
		handleClientStream(world);
	}

	private void handleWorldUpdates(World world) {
		waitingList.sort((u1, u2) -> (int) (u1.getUpdateId() - u2.getUpdateId()));
		long smallestId = Long.MAX_VALUE;
		for (int i = 0; i < waitingList.size(); i++) {
			WorldUpdate worldUpdate = waitingList.get(i);
			history.put(worldUpdate.getUpdateId(), worldUpdate);
			if (worldUpdate.getUpdateId() < smallestId) {
				smallestId = worldUpdate.getUpdateId();
			}
		}
		waitingList.clear();
		history.tailMap(smallestId).values().forEach(u -> {
			world.getMap().addAllChunks(u.getCompressedChunks());
			world.getMap().applyUpdate(u.getStructureUpdates());
			if (u.getEntityUpdateLength() > 0) {
				u.getEntityUpdateByteBuffer().position(0);
				world.getEntityPool().applyUpdate(u.getEntityUpdateByteBuffer());
				float updateDelta = (world.getTime().getTimeMillis() - u.getTime()) / 1000f;
				float deltaTime = world.getTime().getDeltaTime();
				while (updateDelta > deltaTime / 2f) {
					updateDelta -= deltaTime;
					world.getEntityPool().update();
				}
			}
			PlayerEntity playerEntity = client.getMyPlayer();
			playerEntity.getSoundEffectsToConsume().addAll(u.getSoundEffects());
			// Avoid playing multiple times the same sound
			u.getSoundEffects().clear();
			if (u.getLastPlayerMovementRequest().getId() > playerEntity.getLastPlayerMovementRequest().getId()) {
				playerEntity.setLastPlayerMovementRequest(u.getLastPlayerMovementRequest());
			}
			((CooldownAbilityData) playerEntity.getAbilityData(EquipmentAbilityType.WEAPON_BASE.getAbilityId())).setReadyTimeMillis(u.getReadyCooldowns()[0]);
			((CooldownAbilityData) playerEntity.getAbilityData(EquipmentAbilityType.WEAPON_SPECIAL.getAbilityId())).setReadyTimeMillis(u.getReadyCooldowns()[1]);
			((CooldownAbilityData) playerEntity.getAbilityData(EquipmentAbilityType.ACCESSORY1_SPECIAL.getAbilityId())).setReadyTimeMillis(u.getReadyCooldowns()[2]);
			((CooldownAbilityData) playerEntity.getAbilityData(EquipmentAbilityType.ACCESSORY2_SPECIAL.getAbilityId())).setReadyTimeMillis(u.getReadyCooldowns()[3]);
		});

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
			Vector2 myPlayerPosition = client.getMyPlayer().getPosition();
			Vector2 targetPosition = client.getMyPlayer().getTargetPosition();
			clientStream.setTargetAngle(myPlayerPosition.angleToward(targetPosition));
			clientStream.setTargetDistance(myPlayerPosition.distance(targetPosition));
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