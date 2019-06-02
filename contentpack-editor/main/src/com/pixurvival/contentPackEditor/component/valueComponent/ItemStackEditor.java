package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;

public class ItemStackEditor extends ElementEditor<ItemStack> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Item> itemChooser = new ElementChooserButton<>(
			item -> IconService.getInstance().get(item));

	public ItemStackEditor() {
		IntegerInput quantityInput = new IntegerInput(Bounds.min(1));
		if (FileService.getInstance().getCurrentContentPack() != null) {
			itemChooser.setItems(FileService.getInstance().getCurrentContentPack().getItems());
		}

		bind(itemChooser, ItemStack::getItem, ItemStack::setItem);
		bind(quantityInput, ItemStack::getQuantity, ItemStack::setQuantity);

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.fill = GridBagConstraints.BOTH;
		add(itemChooser, gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		add(new JLabel("x"), gbc);
		gbc.gridx++;
		add(quantityInput, gbc);
	}

	public void setItemList(Collection<Item> itemList) {
		itemChooser.setItems(itemList);
	}
}
