package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.contentPack.item.AccessoryItem;

import java.util.ArrayList;
import java.util.List;

public class AccessoryPanel extends EquipablePanel {

    private static final long serialVersionUID = 1L;

    private ItemAlterationAbilityEditor abilityEditor = new ItemAlterationAbilityEditor(true);

    public AccessoryPanel() {
        super(false);
        List<Tab> tabs = new ArrayList<>();
        tabs.add(new Tab(TranslationService.getInstance().getString("accessoryEditor.ability"), abilityEditor));
        finalizeLayout(tabs);
    }

    @Override
    public void bindTo(ItemEditor itemEditor) {
        super.bindTo(itemEditor, AccessoryItem.class);
        itemEditor.bind(abilityEditor, "ability", AccessoryItem.class);
    }

}
