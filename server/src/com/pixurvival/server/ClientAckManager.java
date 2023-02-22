package com.pixurvival.server;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.MathUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages {@link PlayerConnection}s ACKs. ACK are sent by the client
 * to confirm reception of {@link WorldUpdate}s, the purpose of this class is to
 * check if an ACK is missing and, after a delay relative to the estimated ping
 * of the client (computed with ACKs), send a full update. It has the
 * responsibility and exclusivity of {@link PlayerConnection#getWaitingAcks()}
 * attribute.
 *
 * @author SharkHendrix
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAckManager {

    @Getter
    public static class WaitingAckEntry {
        private long time;
        private List<CompressedChunk> compressedChunks;
        private List<StructureUpdate> structureUpdates;
        private List<SoundEffect> soundEffects;

        public WaitingAckEntry(long time, List<CompressedChunk> compressedChunks, List<StructureUpdate> structureUpdates, List<SoundEffect> soundEffects) {
            super();
            this.time = time;
            this.compressedChunks = new ArrayList<>(compressedChunks);
            this.structureUpdates = new ArrayList<>(structureUpdates);
            this.soundEffects = new ArrayList<>(soundEffects);
        }
    }

    public static final float PING_TOLERANCE_MULTIPLIER = 3;

    public static final @Getter ClientAckManager instance = new ClientAckManager();

    private @Getter List<CompressedChunk> compressedChunks = new ArrayList<>();
    private @Getter List<StructureUpdate> structureUpdates = new ArrayList<>();
    private @Getter List<SoundEffect> soundEffects = new ArrayList<>();

    public void addExpectedAck(PlayerGameSession session, WorldUpdate worldUpdate) {
        session.getWaitingAcks().put(worldUpdate.getUpdateId(), new WaitingAckEntry(session.getPlayerEntity().getWorld().getTime().getTimeMillis(), worldUpdate.getCompressedChunks(),
                worldUpdate.getStructureUpdates(), worldUpdate.getSoundEffects()));
    }

    public void acceptAcks(PlayerGameSession session, long[] acks) {
        long time = session.getPlayerEntity().getWorld().getTime().getTimeMillis();
        long pingSum = 0;
        long pingCount = 0;
        for (long ack : acks) {
            WaitingAckEntry entry = session.getWaitingAcks().remove(ack);
            if (entry != null) {
                pingSum += time - entry.time;
                pingCount++;
            }
        }
        if (pingCount != 0) {
            if (session.getPing() == -1) {
                session.setPing((float) pingSum / pingCount);
            } else {
                session.setPing(MathUtils.linearInterpolate(session.getPing(), (float) pingSum / pingCount, 0.1f));
            }
        }
    }

    /**
     * @param session
     * @return true if acks are considered ok, false if at least one ack is
     * considered missing and a full update is required.
     */
    public boolean check(PlayerGameSession session) {
        if (session.getPing() == -1) {
            return true;
        }
        long time = session.getPlayerEntity().getWorld().getTime().getTimeMillis();
        long threshold = (long) (((session.getPing() * PING_TOLERANCE_MULTIPLIER) + GameConstants.CLIENT_STREAM_INTERVAL + 66) * session.getAckThresholdMultiplier());
        boolean ok = true;
        for (WaitingAckEntry entry : session.getWaitingAcks().values()) {
            if (time - entry.time >= threshold) {
                ok = false;
                break;
            }
        }
        compressedChunks.clear();
        structureUpdates.clear();
        soundEffects.clear();
        if (ok) {
            session.setAckThresholdMultiplier(1 + (session.getAckThresholdMultiplier() - 1) * 0.99f);
            return true;
        } else {
            session.setAckThresholdMultiplier(session.getAckThresholdMultiplier() * 1.2f);
            for (WaitingAckEntry entry : session.getWaitingAcks().values()) {
                compressedChunks.addAll(entry.getCompressedChunks());
                // TODO éviter le problème des structureUpadates qui écrasent un
                // plus récent
                structureUpdates.addAll(entry.getStructureUpdates());
                soundEffects.addAll(entry.getSoundEffects());
            }
            session.getWaitingAcks().clear();
            return false;
        }
    }
}
