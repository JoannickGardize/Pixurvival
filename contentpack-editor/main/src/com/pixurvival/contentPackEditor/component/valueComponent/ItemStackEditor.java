package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ItemStackEditor extends ElementEditor<ItemStack> {

    private static final long serialVersionUID = 1L;

    public ItemStackEditor() {
        super(ItemStack.class);
        ElementChooserButton<Item> itemChooser = new ElementChooserButton<>(Item.class);
        IntegerInput quantityInput = new IntegerInput();

        bind(itemChooser, "item");
        bind(quantityInput, "quantity");

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
