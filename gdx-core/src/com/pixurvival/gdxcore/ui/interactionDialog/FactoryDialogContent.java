package com.pixurvival.gdxcore.ui.interactionDialog;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.InventoryTable;

public class FactoryDialogContent extends DialogContent {

	private FactoryStructure currentFactory;
	private InventoryTable recipesTable;
	private InventoryTable fuelsTable;
	private InventoryTable resultsTable;

	@Override
	public boolean build(InteractionDialog dialog) {
		FactoryInteractionDialog factoryDialog = (FactoryInteractionDialog) dialog;
		if (currentFactory != null && currentFactory == factoryDialog.getFactoryStructure()) {
			return false;
		}
		currentFactory = factoryDialog.getFactoryStructure();
		recipesTable = new InventoryTable(factoryDialog.getRecipesInventory(), 4);
		fuelsTable = new InventoryTable(factoryDialog.getFuelsInventory(), 4, factoryDialog.getFactoryStructure().getRecipeSize());
		resultsTable = new InventoryTable(factoryDialog.getResultsInventory(), 4, factoryDialog.getFactoryStructure().getRecipeSize() + factoryDialog.getFactoryStructure().getFuelSize());
		add();
		add(fuelsTable).expand().fill();
		add();
		row();
		add(recipesTable).expand().fill();
		add(new LoadArrow(() -> {
			if (factoryDialog.getActualCraftIndex() == -1) {
				return 0;
			} else {
				long duration = factoryDialog.getFactoryStructure().getCrafts().get(factoryDialog.getActualCraftIndex()).getDuration();
				return 1f - (float) (factoryDialog.getFinishTime() - PixurvivalGame.getWorld().getTime().getTimeMillis()) / (float) duration;
			}
		}));
		add(resultsTable).expand().fill();
		return true;
	}

	@Override
	public void forEachInventories(Consumer<InventoryTable> action) {
		action.accept(recipesTable);
		action.accept(fuelsTable);
		action.accept(resultsTable);
	}

	@Override
	public Actor getAlignActor() {
		return this;
	}

	@Override
	public Class<? extends InteractionDialog> getDialogType() {
		return FactoryInteractionDialog.class;
	}

}
