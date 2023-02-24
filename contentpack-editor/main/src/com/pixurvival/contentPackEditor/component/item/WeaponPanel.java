package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.contentPack.item.WeaponItem;

import java.util.ArrayList;
import java.util.List;

public class WeaponPanel extends EquipablePanel {

    private static final long serialVersionUID = 1L;

    private ItemAlterationAbilityEditor baseAbilityEditor = new ItemAlterationAbilityEditor(true);
    private ItemAlterationAbilityEditor specialAbilityEditor = new ItemAlterationAbilityEditor(true);

    public WeaponPanel() {
        super(true);
        List<Tab> tabs = new ArrayList<>();
        tabs.add(new Tab(TranslationService.getInstance().getString("weaponEditor.baseAbility"), baseAbilityEditor));
        tabs.add(new Tab(TranslationService.getInstance().getString("equipableEditor.specialAbility"), specialAbilityEditor));
        finalizeLayout(tabs);
    }

    @Override
    public void bindTo(ItemEditor itemEditor) {
        super.bindTo(itemEditor, WeaponItem.class);
        itemEditor.bind(baseAbilityEditor, "baseAbility", WeaponItem.class);
        itemEditor.bind(specialAbilityEditor, "specialAbility", WeaponItem.class);
    }
}
