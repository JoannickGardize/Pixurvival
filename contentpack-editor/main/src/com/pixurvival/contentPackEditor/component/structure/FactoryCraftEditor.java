package com.pixurvival.contentPackEditor.component.structure;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.structure.FactoryCraft;
import com.pixurvival.core.item.ItemStack;

import javax.swing.*;

public class FactoryCraftEditor extends ElementEditor<FactoryCraft> {

    private static final long serialVersionUID = 1L;

    public FactoryCraftEditor() {
        super(FactoryCraft.class);
        ListEditor<ItemStack> recipesEditor = new HorizontalListEditor<>(ItemStackEditor::new, ItemStack::new);
        ListEditor<ItemStack> resultsEditor = new HorizontalListEditor<>(ItemStackEditor::new, ItemStack::new);
        TimeInput durationInput = new TimeInput();
        FloatInput fuelConsumptionInput = new FloatInput();

        bind(recipesEditor, "recipes");
        bind(resultsEditor, "results");
        bind(durationInput, "duration");
        bind(fuelConsumptionInput, "fuelConsumption");

        recipesEditor.setBorder(LayoutUtils.createGroupBorder("factoryCraftEditor.recipes"));
        resultsEditor.setBorder(LayoutUtils.createGroupBorder("factoryCraftEditor.results"));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(LayoutUtils.createHorizontalLabelledBox("generic.duration", durationInput, "factoryCraftEditor.fuelConsumption", fuelConsumptionInput));
        add(LayoutUtils.sideBySide(recipesEditor, resultsEditor));
    }

}
