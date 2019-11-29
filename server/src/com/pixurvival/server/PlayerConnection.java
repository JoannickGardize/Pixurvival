package com.pixurvival.server;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.server.ClientAckManager.WaitingAckEntry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection implements InventoryListener {

	private boolean logged = false;
	private PlayerEntity playerEntity = null;
	private boolean gameReady = false;
	private boolean inventoryChanged = true;
	private String requestedTeamName = "Default";
	private boolean requestedFullUpdate = false;
	private long previousClientWorldTime = 0;
	private boolean reconnected = false;
	private Map<Long, WaitingAckEntry> waitingAcks = new HashMap<>();
	private @Getter @Setter boolean spectator = false;

	// TODO Put a big ping every game start, to avoid lot of full updates, when
	// congestion
	private double ping = 300;
	private long nextUpdateId = 0;
	private double ackThresholdMultiplier = 1;

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		inventoryChanged = true;
	}

	public int sendUDP(WorldUpdate worldUpdate) {
		worldUpdate.setUpdateId(nextUpdateId++);
		ClientAckManager.getInstance().addExpectedAck(this, worldUpdate);
		return sendUDP((Object) worldUpdate);
	}
}
