package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ImageFramePreview;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ChangeableTypeBuilder;
import com.pixurvival.contentPackEditor.component.valueComponent.ChangeableTypeEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.item.Item;

public class ItemEditor extends RootElementEditor<Item> {

	private static final long serialVersionUID = 1L;

	private ImageFramePreview itemPreview = new ImageFramePreview();
	private JComboBox<Class<? extends Item.Details>> typeChooser;
	private ChangeableTypeEditor<Item.Details> detailsEditor;

	public ItemEditor() {
		// Contruction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourceEntry::getIcon);
		imageField.setItems(ResourcesService.getInstance().getResources());
		FrameEditor frameEditor = new FrameEditor();
		IntegerInput maxStackSizeInput = new IntegerInput(Bounds.min(1));

		ChangeableTypeBuilder<Item.Details> builder = new ChangeableTypeBuilder<>(Item.class,
				getClass().getPackage().getName(), "item.type");
		typeChooser = builder.getChooser();
		detailsEditor = builder.getEditor();

		imageField.addValueChangeListener(v -> itemPreview.setImage(v.getName()));
		frameEditor.addValueChangeListener(v -> itemPreview.setFrame(v));

		// Binding
		bind(imageField, v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage()),
				(v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(frameEditor, Item::getFrame, Item::setFrame);
		bind(detailsEditor, Item::getDetails, Item::setDetails);
		bind(maxStackSizeInput, Item::getMaxStackSize, Item::setMaxStackSize);

		// Layouting
		setLayout(new GridBagLayout());
		detailsEditor.setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));

		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(LayoutUtils.createGroupBorder("generic.generalProperties"));
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
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.frame", frameEditor, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "itemEditor.maxStackSize", maxStackSizeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", typeChooser, gbc);

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		add(detailsEditor, gbc);

	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this) {
			typeChooser.setSelectedItem(((Item) source.getValue()).getDetails().getClass());
		}
		itemPreview.setImage(getValue().getImage());
		itemPreview.setFrame(getValue().getFrame());
	}
}
