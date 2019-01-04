package com.pixurvival.contentPackEditor.component.itemCraft;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NumberInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.item.ItemStack;

public class ItemCraftEditor extends RootElementEditor<ItemCraft> {

	private static final long serialVersionUID = 1L;

	private ItemStackEditor resultEditor = new ItemStackEditor();
	private ListEditor<ItemStack> recipesList = new ListEditor<>(ItemStackEditor::new, ItemStack::new);

	public ItemCraftEditor() {

		NumberInput<Double> durationField = NumberInput.doubleInput(Bounds.minBounds(0));

		bind(recipesList, ItemCraft::getRecipes, ItemCraft::setRecipes);
		bind(resultEditor, ItemCraft::getResult, ItemCraft::setResult);
		bind(durationField, ItemCraft::getDuration, ItemCraft::setDuration);

		recipesList.setBorder(LayoutUtils.createGroupBorder("itemCraftEditor.recipe"));
		JPanel rightPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(rightPanel, "itemCraftEditor.result", resultEditor, gbc);
		LayoutUtils.addHorizontalLabelledItem(rightPanel, "itemCraftEditor.duration", durationField, gbc);

		LayoutUtils.addSideBySide(this, recipesList, rightPanel);

		EventManager.getInstance().register(this);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		resultEditor.setItemList(event.getContentPack().getItems());
		((ItemStackEditor) recipesList.getEditorForValidation()).setItemList(event.getContentPack().getItems());
	}
}
