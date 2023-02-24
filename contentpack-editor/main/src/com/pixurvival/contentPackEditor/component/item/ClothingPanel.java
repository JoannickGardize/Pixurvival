package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.core.contentPack.item.ClothingItem;

import java.util.Collections;

public class ClothingPanel extends EquipablePanel {

    private static final long serialVersionUID = 1L;

    public ClothingPanel() {
        super(true);
        finalizeLayout(Collections.emptyList());
    }

    @Override
    public void bindTo(ItemEditor itemEditor) {
        super.bindTo(itemEditor, ClothingItem.class);
    }

}
