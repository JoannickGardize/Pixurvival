package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.ui.tooltip.FactoryTooltip;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FactorySlotInputListener extends InputListener {

	private FactoryStructure structure;
	private FactoryTooltip.SlotType slotType;
	private Inventory inventory;
	private int slotIndex;

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		if (inventory.getSlot(slotIndex) == null) {
			FactoryTooltip.getInstance().setData(structure, slotType);
			FactoryTooltip.getInstance().setVisible(true);
		}
		return true;
	}
}
