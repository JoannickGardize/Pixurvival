package com.pixurvival.server;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.ItemCraftDiscoveryListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.*;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.server.ClientAckManager.WaitingAckEntry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PlayerGameSession implements InventoryListener, PlayerConnectionListener, ItemCraftDiscoveryListener {

    private @Getter
    @Setter PlayerConnection connection;
    private @Getter PlayerEntity originalPlayer;
    private @Getter
    @Setter PlayerEntity playerEntity;
    private @Getter Set<ChunkPosition> knownPositions = new HashSet<>();
    private List<CompressedChunk> chunksToSend = new ArrayList<>();
    private List<StructureUpdate> structureUpdatesToSend = new ArrayList<>();
    private Set<ChunkPosition> missingChunks = new HashSet<>();
    private Set<ChunkPosition> newPositions = new HashSet<>();
    private Set<ChunkPosition> oldPositions = new HashSet<>();
    private @Getter
    @Setter boolean gameReady = false;
    private @Getter
    @Setter boolean inventoryChanged = true;
    private @Getter
    @Setter boolean requestedFullUpdate = false;
    private @Getter
    @Setter long previousClientWorldTime = 0;
    private @Getter
    @Setter boolean reconnecting = false;
    private @Getter
    @Setter Map<Long, WaitingAckEntry> waitingAcks = new HashMap<>();
    private @Getter
    @Setter boolean spectator = false;
    private @Getter
    @Setter float smoothedTimeDiff;
    private @Getter
    @Setter float ping = -1;
    private @Getter
    @Setter long nextUpdateId = 0;
    private @Getter
    @Setter float ackThresholdMultiplier = 1;
    private @Setter NetworkActivityListener networkListener;

    public PlayerGameSession(PlayerConnection connection, PlayerEntity playerEntity) {
        this.connection = connection;
        this.playerEntity = playerEntity;
        this.originalPlayer = playerEntity;
    }

    public void resetNetworkData() {
        previousClientWorldTime = 0;
        smoothedTimeDiff = 0;
        ping = -1;
        nextUpdateId = 0;
        ackThresholdMultiplier = 1;
    }

    public int sendUDP(WorldUpdate worldUpdate) {
        worldUpdate.setUpdateId(nextUpdateId++);
        ClientAckManager.getInstance().addExpectedAck(this, worldUpdate);
        return sendUDP((Object) worldUpdate);
    }

    public int sendUDP(Object object) {
        int size = connection.sendUDP(object);
        if (networkListener != null) {
            networkListener.sent(this, object, size);
        }
        return size;
    }

    @Override
    public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
        inventoryChanged = true;
    }

    public void addNewPosition(ChunkPosition position) {
        newPositions.add(position);
        oldPositions.remove(position);
    }

    public void addOldPosition(ChunkPosition position) {
        oldPositions.add(position);
        newPositions.remove(position);
    }

    public void foreachOldPosition(Consumer<ChunkPosition> action) {
        oldPositions.forEach(action);
    }

    public void addChunkIfNotKnown(Chunk chunk) {
        if (knownPositions.add(chunk.getPosition())) {
            chunksToSend.add(chunk.getCompressed());
        }
    }

    public void invalidateChunk(ChunkPosition position) {
        knownPositions.remove(position);
    }

    public void addMissingChunk(ChunkPosition position) {
        missingChunks.add(position);
    }

    public void addStructureUpdate(StructureUpdate structureUpdate) {
        structureUpdatesToSend.add(structureUpdate);
    }

    public boolean isMissingAndRemove(ChunkPosition position) {
        return missingChunks.remove(position);
    }

    public boolean isNewPosition(ChunkPosition position) {
        return newPositions.contains(position);
    }

    public boolean isOldPosition(ChunkPosition position) {
        return oldPositions.contains(position);
    }

    public void clearPositionChanges() {
        newPositions.clear();
        oldPositions.clear();
    }

    public void extractChunksToSend(List<CompressedChunk> list) {
        list.addAll(chunksToSend);
        chunksToSend.clear();
    }

    public void extractStructureUpdatesToSend(List<StructureUpdate> list) {
        list.addAll(structureUpdatesToSend);
        structureUpdatesToSend.clear();
    }

    public void clearChunkAndStructureUpdates() {
        chunksToSend.clear();
        structureUpdatesToSend.clear();
    }

    @Override
    public void handleGameReady(GameReady gameReady) {
        setGameReady(true);
        if (isReconnecting()) {
            getConnection().sendTCP(new StartGame(playerEntity.getWorld().getTime().getTimeMillis()));
            setReconnecting(false);
        }
    }

    @Override
    public void handleRefreshRequest(RefreshRequest refreshRequest) {
        setRequestedFullUpdate(true);
    }

    @Override
    public void handlePlayerActionRequest(IPlayerActionRequest playerActionRequest) {
        if (!spectator && playerEntity != null && playerEntity.isAlive()) {
            playerActionRequest.apply(playerEntity);
        } else if (spectator && playerActionRequest instanceof ChatRequest) {
            playerActionRequest.apply(originalPlayer);
        }
    }

    @Override
    public void handleClientStream(ClientStream clientStream) {
        ClientAckManager.getInstance().acceptAcks(this, clientStream.getAcks());
        if (clientStream.getTime() > previousClientWorldTime) {
            previousClientWorldTime = clientStream.getTime();
            if (!spectator) {
                playerEntity.getTargetPosition().set(playerEntity.getPosition()).addEuclidean(clientStream.getTargetDistance(), clientStream.getTargetAngle());
            }
        }
        smoothedTimeDiff = MathUtils.linearInterpolate(smoothedTimeDiff, clientStream.getTime() - playerEntity.getWorld().getTime().getTimeMillis() + ping / 2, 0.1f);
        if (Math.abs(smoothedTimeDiff) > 10) {
            connection.sendUDP(new TimeSync(clientStream.getTime(), playerEntity.getWorld().getTime().getTimeMillis()));
        }
    }

    @Override
    public void discovered(Collection<ItemCraft> itemCrafts) {
        connection.sendTCP(new ItemCraftAvailable(itemCrafts));
    }
}
