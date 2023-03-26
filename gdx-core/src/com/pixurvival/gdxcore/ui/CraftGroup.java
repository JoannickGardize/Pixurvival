package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.contentPack.item.ItemCraft;

import java.util.List;

public class CraftGroup extends HorizontalGroup {

    public CraftGroup(List<ItemCraft> itemCrafts) {
        wrap(true);
        wrapSpace(2);
        pad(2);
        space(2);
        align(Align.center);
        rowAlign(Align.left);
        for (ItemCraft itemCraft : itemCrafts) {
            addSlot(itemCraft, false);
        }
    }

    public void addSlot(ItemCraft itemCraft, boolean newlyDiscovered) {
        for (int i = 0; i < getChildren().size; i++) {
            int otherId = ((CraftSlot) getChild(i)).getItemCraft().getId();
            if (itemCraft.getId() == otherId) {
                return;
            } else if (itemCraft.getId() < otherId) {
                addActorAt(i, new CraftSlot(itemCraft, newlyDiscovered));
                return;
            }
        }
        addActor(new CraftSlot(itemCraft, newlyDiscovered));
    }

    public void updateCraftStates() {
        if (hasParent()) {
            getChildren().forEach(a -> ((CraftSlot) a).updateState());
        }
    }
}