package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ImageFramePreview;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.item.AccessoryItem;
import com.pixurvival.core.item.ClothingItem;
import com.pixurvival.core.item.EdibleItem;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ResourceItem;
import com.pixurvival.core.item.StructureItem;
import com.pixurvival.core.item.WeaponItem;

public class ItemEditor extends InstanceChangingRootElementEditor<Item> {

	private static final long serialVersionUID = 1L;

	private ImageFramePreview itemPreview = new ImageFramePreview();

	public ItemEditor() {
		super("itemType");

		// Contruction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourceEntry::getIcon);
		imageField.setItems(ResourcesService.getInstance().getResources());
		FrameEditor frameEditor = new FrameEditor();
		IntegerInput maxStackSizeInput = new IntegerInput(Bounds.min(1));

		imageField.addValueChangeListener(v -> itemPreview.setImage(v.getName()));
		frameEditor.addValueChangeListener(v -> itemPreview.setFrame(v));

		// Binding
		bind(imageField, v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage()), (v, f) -> v.setImage(f == null ? null : f.getName()));
		bind(frameEditor, Item::getFrame, Item::setFrame);
		bind(maxStackSizeInput, Item::getMaxStackSize, Item::setMaxStackSize);

		// Layouting
		setLayout(new GridBagLayout());

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
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", getTypeChooser(), gbc);

		JPanel propertiesPanel = new JPanel(new GridBagLayout());
		propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));
		LayoutUtils.addVertically(this, northPanel, propertiesPanel);

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		propertiesPanel.add(getSpecificPartPanel(), gbc);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		super.valueChanged(source);
		itemPreview.setImage(getValue().getImage());
		itemPreview.setFrame(getValue().getFrame());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		entries.add(new ClassEntry(ResourceItem.class, new JPanel()));

		EdiblePanel ediblePanel = new EdiblePanel();
		ediblePanel.bindTo(this);
		entries.add(new ClassEntry(EdibleItem.class, ediblePanel));

		WeaponPanel weaponPanel = new WeaponPanel();
		weaponPanel.bindTo(this);
		entries.add(new ClassEntry(WeaponItem.class, weaponPanel));

		AccessoryPanel accessoryPanel = new AccessoryPanel();
		accessoryPanel.bindTo(this);
		entries.add(new ClassEntry(AccessoryItem.class, accessoryPanel));

		ClothingPanel clothingPanel = new ClothingPanel();
		clothingPanel.bindTo(this);
		entries.add(new ClassEntry(ClothingItem.class, clothingPanel));

		StructurePanel structurePanel = new StructurePanel();
		structurePanel.bindTo(this);
		entries.add(new ClassEntry(StructureItem.class, structurePanel));

		return entries;
	}

	@Override
	protected void initialize(Item oldInstance, Item newInstance) {
		super.initialize(oldInstance, newInstance);
		newInstance.setFrame(oldInstance.getFrame());
		newInstance.setMaxStackSize(oldInstance.getMaxStackSize());
		newInstance.setImage(oldInstance.getImage());
	}
}
