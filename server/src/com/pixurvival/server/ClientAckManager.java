package com.pixurvival.server;

import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.MathUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

	public enum CheckResult {
		OK,
		REQUIRE_ENTITY,
		REQUIRE_FULL;
	}

	@AllArgsConstructor
	public class WaitingAckEntry {
		public long time;
		public boolean containsChunk;
	}

	public static final double PING_TOLERANCE_MULTIPLIER = 3;

	public static final @Getter ClientAckManager instance = new ClientAckManager();

	public void addExpectedAck(PlayerConnection connection, WorldUpdate worldUpdate) {
		connection.getWaitingAcks().put(worldUpdate.getUpdateId(),
				new WaitingAckEntry(connection.getPlayerEntity().getWorld().getTime().getTimeMillis(), !worldUpdate.getStructureUpdates().isEmpty() || !worldUpdate.getCompressedChunks().isEmpty()));
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
			}
		}
		if (pingCount != 0) {
			connection.setPing(MathUtils.linearInterpolate(connection.getPing(), (double) pingSum / pingCount, 0.3));
		}
	}

	/**
	 * @param connection
	 * @return true if acks are considered ok, false if ack is considered
	 *         missing and a full update is required.
	 */
	public CheckResult check(PlayerConnection connection) {
		long time = connection.getPlayerEntity().getWorld().getTime().getTimeMillis();
		long threshold = (long) (connection.getPing() * PING_TOLERANCE_MULTIPLIER);
		boolean ok = true;
		for (WaitingAckEntry entry : connection.getWaitingAcks().values()) {
			if (time - entry.time >= threshold) {
				ok = false;
			}
		}
		if (ok) {
			return CheckResult.OK;
		} else {
			boolean requireChunks = false;
			for (WaitingAckEntry entry : connection.getWaitingAcks().values()) {
				requireChunks = requireChunks || entry.containsChunk;
			}
			connection.getWaitingAcks().clear();
			if (requireChunks) {
				return CheckResult.REQUIRE_FULL;
			} else {
				return CheckResult.REQUIRE_ENTITY;
			}
		}
	}
}
