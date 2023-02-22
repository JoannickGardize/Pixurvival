package com.pixurvival.contentPackEditor.component.structure;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.structure.FactoryFuel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class FactoryFuelEditor extends ElementEditor<FactoryFuel> {

    private static final long serialVersionUID = 1L;

    public FactoryFuelEditor() {
        super(FactoryFuel.class);

        ElementChooserButton<Item> itemChooser = new ElementChooserButton<>(Item.class);
        FloatInput amountInput = new FloatInput();

        bind(itemChooser, "item");
        bind(amountInput, "amount");

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        LayoutUtils.addHorizontallyLabelled(this, "factoryFuelEditor.fuel", itemChooser, "factoryFuelEditor.amount", amountInput);
    }

}
