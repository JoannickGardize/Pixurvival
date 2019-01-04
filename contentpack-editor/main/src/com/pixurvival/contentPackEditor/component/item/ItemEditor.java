package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NumberInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.core.item.Item;

public class ItemEditor extends RootElementEditor<Item> {

	private static final long serialVersionUID = 1L;

	private ItemPreview itemPreview;
	private JComboBox<ItemType> typeChooser = new JComboBox<>(ItemType.values());

	public ItemEditor() {
		// Contruction
		itemPreview = new ItemPreview();
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourceEntry::getIcon);
		imageField.setItems(ResourcesService.getInstance().getResources());
		FrameEditor frameEditor = new FrameEditor();
		ItemDetailsEditor detailsEditor = new ItemDetailsEditor();
		typeChooser = new JComboBox<>(ItemType.values());
		NumberInput<Integer> maxStackSizeInput = NumberInput.integerInput(Bounds.minBounds(1));

		// actions

		typeChooser.addActionListener(e -> {
			detailsEditor.changeType((ItemType) typeChooser.getSelectedItem());
		});

		// Binding
		bind(imageField, v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage()), (v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(frameEditor, Item::getFrame, Item::setFrame);
		bind(detailsEditor, Item::getDetails, Item::setDetails);
		bind(maxStackSizeInput, Item::getMaxStackSize, Item::setMaxStackSize);

		// Layouting
		setLayout(new GridBagLayout());
		detailsEditor.setBorder(LayoutUtils.createGroupBorder("itemEditor.typeProperties"));

		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(LayoutUtils.createGroupBorder("itemEditor.generalProperties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets.top = 4;
		gbc.insets.bottom = 4;
		northPanel.add(itemPreview, gbc);
		gbc.fill = GridBagConstraints.NONE;
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.image", imageField, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "itemEditor.frame", frameEditor, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "itemEditor.maxStackSize", maxStackSizeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "item.type", typeChooser, gbc);

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		add(detailsEditor, gbc);

	}

	@Override
	protected void valueChanged() {
		typeChooser.setSelectedItem(ItemType.forClass(getValue().getDetails().getClass()));
		itemPreview.setItem(getValue());
	}
}
