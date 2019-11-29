package com.pixurvival.server;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.MathUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This class manages {@link PlayerConnection}s ACKs. ACK are sent by the client
 * to confirm reception of {@link WorldUpdate}s, the purpose of this class is to
 * check if an ACK is missing and, after a delay relative to the estimated ping
 * of the client (computed with ACKs), send a full update. It has the
 * responsibility and exclusivity of {@link PlayerConnection#getWaitingAcks()}
 * attribute.
 * 
 * TODO packet loss statistics for congestion management
 * 
 * @author SharkHendrix
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAckManager {

	@Getter
	public static class WaitingAckEntry {
		private long time;
		private List<CompressedChunk> compressedChunks;
		private List<StructureUpdate> structureUpdates;

		public WaitingAckEntry(long time, List<CompressedChunk> compressedChunks, List<StructureUpdate> structureUpdates) {
			super();
			this.time = time;
			this.compressedChunks = new ArrayList<>(compressedChunks);
			this.structureUpdates = new ArrayList<>(structureUpdates);
		}

	}

	public static final double PING_TOLERANCE_MULTIPLIER = 3;

	public static final @Getter ClientAckManager instance = new ClientAckManager();

	private @Getter List<CompressedChunk> compressedChunks = new ArrayList<>();
	private @Getter List<StructureUpdate> structureUpdates = new ArrayList<>();

	public void addExpectedAck(PlayerConnection connection, WorldUpdate worldUpdate) {
		connection.getWaitingAcks().put(worldUpdate.getUpdateId(),
				new WaitingAckEntry(connection.getPlayerEntity().getWorld().getTime().getTimeMillis(), worldUpdate.getCompressedChunks(), worldUpdate.getStructureUpdates()));
	}

	public void acceptAcks(PlayerConnection connection, long[] acks) {
		long time = connection.getPlayerEntity().getWorld().getTime().getTimeMillis();
		long pingSum = 0;
		long pingCount = 0;
		for (long ack : acks) {
			WaitingAckEntry entry = connection.getWaitingAcks().remove(ack);
			if (entry != null) {
				pingSum += time - entry.time;
				pingCount++;
			} else {
			}
		}
		if (pingCount != 0) {
			connection.setPing(MathUtils.linearInterpolate(connection.getPing(), (double) pingSum / pingCount, 0.1));
		}
	}

	/**
	 * @param connection
	 * @return true if acks are considered ok, false if ack is considered
	 *         missing and a full update is required.
	 */
	public boolean check(PlayerConnection connection) {
		long time = connection.getPlayerEntity().getWorld().getTime().getTimeMillis();
		long threshold = (long) (((connection.getPing() * PING_TOLERANCE_MULTIPLIER) + GameConstants.CLIENT_STREAM_INTERVAL) * connection.getAckThresholdMultiplier());
		boolean ok = true;
		for (WaitingAckEntry entry : connection.getWaitingAcks().values()) {
			if (time - entry.time >= threshold) {
				ok = false;
				break;
			}
		}
		if (ok) {
			connection.setAckThresholdMultiplier(1 + (connection.getAckThresholdMultiplier() - 1) * 0.99);
			return true;
		} else {
			connection.setAckThresholdMultiplier(connection.getAckThresholdMultiplier() * 1.2);
			compressedChunks.clear();
			structureUpdates.clear();
			for (WaitingAckEntry entry : connection.getWaitingAcks().values()) {
				compressedChunks.addAll(entry.getCompressedChunks());
				// TODO éviter le problème des structureUpadates qui écrasent un
				// plus récent
				structureUpdates.addAll(entry.getStructureUpdates());
			}
			connection.getWaitingAcks().clear();
			return false;
		}
	}
}
