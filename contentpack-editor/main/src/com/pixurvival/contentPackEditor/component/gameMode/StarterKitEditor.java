package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.core.contentPack.gameMode.role.StarterKit;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.ItemStack;

import java.awt.*;

public class StarterKitEditor extends ElementEditor<StarterKit> {

    private static final long serialVersionUID = 1L;

    public StarterKitEditor() {
        super(StarterKit.class);
        ElementChooserButton<WeaponItem> weaponChooser = new ElementChooserButton<>(WeaponItem.class);
        ElementChooserButton<ClothingItem> clothingChooser = new ElementChooserButton<>(ClothingItem.class);
        ElementChooserButton<AccessoryItem> accessory1Chooser = new ElementChooserButton<>(AccessoryItem.class);
        ElementChooserButton<AccessoryItem> accessory2Chooser = new ElementChooserButton<>(AccessoryItem.class);
        ListEditor<ItemStack> inventoryEditor = new HorizontalListEditor<>(ItemStackEditor::new, ItemStack::new);
        inventoryEditor.setBorder(LayoutUtils.createGroupBorder("starterKitEditor.inventory"));

        bind(weaponChooser, "weaponItem");
        bind(clothingChooser, "clothingItem");
        bind(accessory1Chooser, "accessory1Item");
        bind(accessory2Chooser, "accessory2Item");
        bind(inventoryEditor, "inventory");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "itemType.weaponItem", weaponChooser, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "itemType.accessoryItem", accessory1Chooser, gbc);
        LayoutUtils.nextColumn(gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "itemType.clothingItem", clothingChooser, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "itemType.accessoryItem", accessory2Chooser, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 4;
        add(inventoryEditor, gbc);
    }
}
