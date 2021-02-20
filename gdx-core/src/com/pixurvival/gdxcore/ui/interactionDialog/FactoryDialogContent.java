package com.pixurvival.gdxcore.ui.interactionDialog;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.InventoryTable;
import com.pixurvival.gdxcore.ui.tooltip.FactoryTooltip;
import com.pixurvival.gdxcore.util.FloatSupplier;

public class FactoryDialogContent extends DialogContent {

	private InventoryTable recipesTable;
	private InventoryTable fuelsTable;
	private InventoryTable resultsTable;

	@Override
	public boolean build(InteractionDialog dialog) {
		FactoryInteractionDialog factoryDialog = (FactoryInteractionDialog) dialog;
		FactoryStructure factoryStructure = factoryDialog.getFactoryStructure();
		if (recipesTable != null && recipesTable.getInventory() == factoryDialog.getRecipesInventory()) {
			return false;
		}
		recipesTable = new InventoryTable(factoryDialog.getRecipesInventory(), 4) {
			@Override
			public Actor newSlot(Inventory inventory, int index, int actionIndex) {
				Actor result = super.newSlot(inventory, index, actionIndex);
				result.addListener(new FactorySlotInputListener(factoryStructure, FactoryTooltip.SlotType.RECIPE, factoryDialog.getRecipesInventory(), index));
				return result;
			}
		};
		resultsTable = new InventoryTable(factoryDialog.getResultsInventory(), 4, factoryStructure.getRecipeSize() + factoryStructure.getFuelSize());
		FloatSupplier workProgressSupplier = () -> {
			if (factoryDialog.getActualCraftIndex() == -1) {
				return 0;
			} else {
				long duration = factoryStructure.getCrafts().get(factoryDialog.getActualCraftIndex()).getDuration();
				return 1f - (float) (factoryDialog.getFinishTime() - PixurvivalGame.getWorld().getTime().getTimeMillis()) / (float) duration;
			}
		};
		defaults().pad(2);
		clearChildren();
		if (!factoryStructure.getFuels().isEmpty()) {
			fuelsTable = new InventoryTable(factoryDialog.getFuelsInventory(), 4, factoryStructure.getRecipeSize()) {
				@Override
				public Actor newSlot(Inventory inventory, int index, int actionIndex) {
					Actor result = super.newSlot(inventory, index, actionIndex);
					result.addListener(new FactorySlotInputListener(factoryStructure, FactoryTooltip.SlotType.FUEL, factoryDialog.getFuelsInventory(), index));
					return result;
				}
			};
			add();
			add(fuelsTable).expand().fill();
			add(new UnloadVerticalBar(() -> (factoryDialog.getFuelTank()
					+ (factoryDialog.getActualCraftIndex() == -1 ? 0 : (1f - workProgressSupplier.get()) * factoryStructure.getCrafts().get(factoryDialog.getActualCraftIndex()).getFuelConsumption()))
					/ factoryStructure.getMaxTankFuel())).left();
			row();
		}
		add(recipesTable).expand().fill();
		add(new LoadHorizontalArrow(workProgressSupplier));
		add(resultsTable).expand().fill();
		return true;
	}

	@Override
	public void forEachInventories(Consumer<InventoryTable> action) {
		action.accept(recipesTable);
		if (fuelsTable != null) {
			action.accept(fuelsTable);
		}
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
