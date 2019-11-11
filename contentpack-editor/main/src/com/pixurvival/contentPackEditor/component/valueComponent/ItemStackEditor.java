package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;

public class ItemStackEditor extends ElementEditor<ItemStack> {

	private static final long serialVersionUID = 1L;

	public ItemStackEditor() {
		this(true);
	}

	public ItemStackEditor(boolean itemRequired) {
		ElementChooserButton<Item> itemChooser = new ElementChooserButton<>(Item.class, itemRequired);
		IntegerInput quantityInput = new IntegerInput(Bounds.min(1));

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

}
