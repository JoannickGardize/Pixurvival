package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialogHolder;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.MultiInventoryHolder;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.FactoryStructureUpdate;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.LongSequenceIOHelper;

import lombok.Getter;
import lombok.Setter;

public class FactoryStructureEntity extends StructureEntity implements InteractionDialogHolder, MultiInventoryHolder {

	private @Getter FactoryInteractionDialog interactionDialog;
	private @Getter @Setter boolean waitingInput;
	private @Getter @Setter boolean waitingOutput;
	private @Getter boolean working;

	public FactoryStructureEntity(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		interactionDialog = new FactoryInteractionDialog(this, (FactoryStructure) definition);
		interactionDialog.getRecipesInventory().addListener((i, s, p, n) -> updateWaitingInput());
		interactionDialog.getFuelsInventory().addListener((i, s, p, n) -> updateWaitingInput());
		interactionDialog.getResultsInventory().addListener((i, s, p, n) -> updateWaitingOutput());
	}

	public void setWorking(boolean working) {
		if (working != this.working) {
			this.working = working;
			getChunk().notifyStructureChanged(this, new FactoryStructureUpdate(getTileX(), getTileY(), getId(), working));
		}
	}

	@Override
	public void writeData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.writeData(buffer, idSequence);
		FactoryInteractionDialog.write(buffer, interactionDialog, getWorld());
		ByteBufferUtils.putBoolean(buffer, working);
	}

	@Override
	public void applyData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.applyData(buffer, idSequence);
		FactoryInteractionDialog.read(buffer, interactionDialog, getWorld());
		working = ByteBufferUtils.getBoolean(buffer);
	}

	@Override
	public void setInteractionDialog(InteractionDialog interactionDialog) {
		if (interactionDialog instanceof FactoryInteractionDialog) {
			this.interactionDialog = (FactoryInteractionDialog) interactionDialog;
		}
	}

	@Override
	public void onDeath() {
		((FactoryStructure) getDefinition()).getItemHandlingOnDeath().getHandler().accept(this);
	}

	private void updateWaitingInput() {
		waitingInput = false;
		getChunk().updateTimestamp();
	}

	private void updateWaitingOutput() {
		waitingOutput = false;
		getChunk().updateTimestamp();
	}

	@Override
	public void forEachInventory(Consumer<Inventory> action) {
		action.accept(interactionDialog.getRecipesInventory());
		action.accept(interactionDialog.getFuelsInventory());
		action.accept(interactionDialog.getResultsInventory());
	}
}