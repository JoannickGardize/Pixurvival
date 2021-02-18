package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.structure.InventoryStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialogHolder;
import com.pixurvival.core.interactionDialog.InventoryInteractionDialog;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.util.LongSequenceIOHelper;

import lombok.Getter;
import lombok.Setter;

public class InventoryStructureEntity extends StructureEntity implements InteractionDialogHolder, InventoryHolder {

	private @Getter Inventory inventory;
	private @Getter @Setter InteractionDialog interactionDialog;

	public InventoryStructureEntity(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		inventory = new Inventory(((InventoryStructure) definition).getInventorySize());
		interactionDialog = new InventoryInteractionDialog(this, inventory);
		inventory.addListener((i, s, p, n) -> getChunk().updateTimestamp());
	}

	@Override
	public void writeData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.writeData(buffer, idSequence);
		inventory.write(buffer);
	}

	@Override
	public void applyData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.applyData(buffer, idSequence);
		inventory.apply(getWorld(), buffer);
	}

	@Override
	public void onDeath() {
		((InventoryStructure) getDefinition()).getItemHandlingOnDeath().getHandler().accept(this);
	}
}
