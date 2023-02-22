package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Scene2dUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class InventorySlot extends Button {

    private @Getter Inventory inventory;
    private int slotIndex;
    private @Getter int actionIndex;
    private ItemStackDrawer itemStackDrawer;
    private @Setter ShortcutDrawer shortcutDrawer;

    public InventorySlot(Inventory inventory, int slotIndex) {
        this(inventory, slotIndex, slotIndex);
    }

    public InventorySlot(Inventory inventory, int slotIndex, int actionIndex) {
        super(PixurvivalGame.getSkin());
        this.inventory = inventory;
        this.slotIndex = slotIndex;
        this.actionIndex = actionIndex;

        itemStackDrawer = new ItemStackDrawer(this, 2);
        this.addListener(new InventorySlotInputListener(inventory, slotIndex, actionIndex));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        ItemStack newItem = inventory.getSlot(slotIndex);
        // TODO inventory listener for this instead
        if (newItem != null && !Objects.equals(itemStackDrawer.getItemStack(), newItem)) {
            addAction(Scene2dUtils.yellowLightning());
        }
        itemStackDrawer.setItemStack(newItem);
        itemStackDrawer.draw(batch);
        if (shortcutDrawer != null) {
            shortcutDrawer.draw(batch);
        }
    }

    @Override
    public String toString() {
        return "InventorySlot " + slotIndex;
    }
}