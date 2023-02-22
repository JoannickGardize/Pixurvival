package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UseItemInInventoryProcessor implements InputActionProcessor {

    private int slotIndex;

    @Override
    public void buttonDown() {
        ItemStack slotContent = PixurvivalGame.getClient().getMyInventory().getSlot(slotIndex);
        if (slotContent != null && (slotContent.getItem() instanceof EdibleItem || Equipment.canEquipAnywhere(slotContent))) {
            PixurvivalGame.getClient().sendAction(new UseItemRequest(slotIndex));

        }
    }
}
