package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.core.contentPack.gameMode.role.StarterKit;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.ItemStack;

public class StarterKitEditor extends ElementEditor<StarterKit> {

	private static final long serialVersionUID = 1L;

	public StarterKitEditor() {
		ElementChooserButton<WeaponItem> weaponChooser = new ElementChooserButton<>(WeaponItem.class, false);
		ElementChooserButton<ClothingItem> clothingChooser = new ElementChooserButton<>(ClothingItem.class, false);
		ElementChooserButton<AccessoryItem> accessory1Chooser = new ElementChooserButton<>(AccessoryItem.class, false);
		ElementChooserButton<AccessoryItem> accessory2Chooser = new ElementChooserButton<>(AccessoryItem.class, false);
		ListEditor<ItemStack> inventoryEditor = new HorizontalListEditor<>(ItemStackEditor::new, ItemStack::new);
		inventoryEditor.setBorder(LayoutUtils.createGroupBorder("starterKitEditor.inventory"));

		bind(weaponChooser, StarterKit::getWeaponItem, StarterKit::setWeaponItem);
		bind(clothingChooser, StarterKit::getClothingItem, StarterKit::setClothingItem);
		bind(accessory1Chooser, StarterKit::getAccessory1Item, StarterKit::setAccessory2Item);
		bind(accessory2Chooser, StarterKit::getAccessory1Item, StarterKit::setAccessory2Item);
		bind(inventoryEditor, StarterKit::getInventory, StarterKit::setInventory);

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
