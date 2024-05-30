package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.contentPack.item.ItemCraft;

public class CraftGroup extends HorizontalGroup {

    public CraftGroup() {
        wrap(true);
        wrapSpace(2);
        pad(2);
        space(2);
        align(Align.center);
        rowAlign(Align.left);
    }

    public CraftSlot addSlot(ItemCraft itemCraft, boolean newlyDiscovered) {
        for (int i = 0; i < getChildren().size; i++) {
            int otherId = ((CraftSlot) getChild(i)).getItemCraft().getId();
            if (itemCraft.getId() == otherId) {
                return null;
            } else if (itemCraft.getId() < otherId) {
                CraftSlot craftSlot = new CraftSlot(itemCraft, newlyDiscovered);
                addActorAt(i, craftSlot);
                return craftSlot;
            }
        }
        CraftSlot craftSlot = new CraftSlot(itemCraft, newlyDiscovered);
        addActor(craftSlot);
        return craftSlot;
    }

    public void updateCraftStates() {
        if (hasParent()) {
            getChildren().forEach(a -> ((CraftSlot) a).updateState());
        }
    }
}