package com.pixurvival.contentPackEditor.component.itemCraft;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.item.ItemStack;

public class ItemCraftEditor extends RootElementEditor<ItemCraft> {

	private static final long serialVersionUID = 1L;

	public ItemCraftEditor() {
		ItemStackEditor resultEditor = new ItemStackEditor();
		VerticalListEditor<ItemStack> recipesList = new VerticalListEditor<>(ItemStackEditor::new, ItemStack::new, VerticalListEditor.HORIZONTAL);
		TimeInput durationField = new TimeInput();
		ElementChooserButton<Structure> requiredStructureChooser = new ElementChooserButton<>(Structure.class, false);

		bind(recipesList, ItemCraft::getRecipes, ItemCraft::setRecipes);
		bind(resultEditor, ItemCraft::getResult, ItemCraft::setResult);
		bind(durationField, ItemCraft::getDuration, ItemCraft::setDuration);
		bind(requiredStructureChooser, ItemCraft::getRequiredStructure, ItemCraft::setRequiredStructure);

		recipesList.setBorder(LayoutUtils.createGroupBorder("itemCraftEditor.recipe"));
		JPanel rightPanel = LayoutUtils.createVerticalLabelledBox("itemCraftEditor.result", resultEditor, "itemCraftEditor.duration", durationField, "itemCraftEditor.requiredStructure",
				requiredStructureChooser);

		LayoutUtils.addSideBySide(this, recipesList, rightPanel);
	}
}
