package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.Getter;

import java.util.Collection;
import java.util.Locale;

public class FactoryTooltip extends Tooltip implements InventoryListener {

    private static @Getter FactoryTooltip instance = new FactoryTooltip();

    public enum SlotType {
        RECIPE,
        FUEL
    }

    private FactoryStructure structure;

    private SlotType slotType;
    private boolean mustBeShown = false;

    private FactoryTooltip() {
        setTouchable(Touchable.disabled);
        setVisible(false);
        setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
        defaults().fill().pad(2);
    }

    public void setData(FactoryStructure structure, SlotType slotType) {
        if (this.structure != structure || this.slotType != slotType) {
            this.structure = structure;
            this.slotType = slotType;
            build();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (mustBeShown) {
            super.setVisible(visible);
        } else {
            super.setVisible(false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        DrawUtils.setTooltipPosition(this);
        super.draw(batch, parentAlpha);
    }

    private void build() {
        Collection<Item> items = slotType == SlotType.RECIPE ? structure.getPossibleRecipes() : structure.getPossibleFuels();
        Locale locale = PixurvivalGame.getClient().getCurrentLocale();
        ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
        PlayerInventory inv = PixurvivalGame.getClient().getMyInventory();
        clearChildren();
        mustBeShown = false;
        for (Item item : items) {
            if (inv.contains(item, 1) || inv.getHeldItemStack() != null && inv.getHeldItemStack().getItem() == item) {
                Texture itemTexture = PixurvivalGame.getContentPackTextures().getItem(item.getId()).getTexture();
                add(new Image(itemTexture)).size(RepresenterUtils.ITEM_WIDTH, RepresenterUtils.ITEM_WIDTH);
                Label nameLabel = new Label(contentPack.getTranslation(locale, item, TranslationKey.NAME), PixurvivalGame.getSkin(), "white");
                add(nameLabel).expand();
                row();
                mustBeShown = true;
            }
        }
        invalidate();
        pack();
    }

    @Override
    public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
        if (isVisible()) {
            build();
        } else {
            slotType = null;
        }
    }
}
