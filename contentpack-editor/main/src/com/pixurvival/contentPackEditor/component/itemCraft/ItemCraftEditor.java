package com.pixurvival.contentPackEditor.component.itemCraft;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.item.ItemStack;

import javax.swing.*;

public class ItemCraftEditor extends RootElementEditor<ItemCraft> {

    private static final long serialVersionUID = 1L;

    public ItemCraftEditor() {
        super(ItemCraft.class);
        ItemStackEditor resultEditor = new ItemStackEditor();
        VerticalListEditor<ItemStack> recipesList = new VerticalListEditor<>(ItemStackEditor::new, ItemStack::new, VerticalListEditor.HORIZONTAL);
        TimeInput durationField = new TimeInput();
        ElementChooserButton<Structure> requiredStructureChooser = new ElementChooserButton<>(Structure.class);
        ListEditor<Item> discoveryItemsList = new HorizontalListEditor<>(() -> new ElementChooserButton<>(Item.class), () -> null);

        bind(recipesList, "recipes");
        bind(resultEditor, "result");
        bind(durationField, "duration");
        bind(requiredStructureChooser, "requiredStructure");
        bind(discoveryItemsList, "discoveryItems");

        recipesList.setBorder(LayoutUtils.createGroupBorder("itemCraftEditor.recipe"));
        discoveryItemsList.setBorder(LayoutUtils.createGroupBorder("itemCraftEditor.discoveryItems"));
        JPanel leftPanel = LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 0, recipesList, discoveryItemsList);
        JPanel rightPanel = LayoutUtils.createVerticalLabelledBox("itemCraftEditor.result", resultEditor, "itemCraftEditor.duration", durationField, "itemCraftEditor.requiredStructure",
                requiredStructureChooser);

        LayoutUtils.addSideBySide(this, leftPanel, rightPanel);
    }
}
