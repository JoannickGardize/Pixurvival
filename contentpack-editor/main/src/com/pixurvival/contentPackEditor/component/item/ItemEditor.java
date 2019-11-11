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
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview.ClickEvent;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.InteractionListener;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FrameEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;

public class ItemEditor extends InstanceChangingRootElementEditor<Item> implements InteractionListener {

	private static final long serialVersionUID = 1L;

	private ImageFramePreview itemPreview = new ImageFramePreview();
	private ItemFrameChooserPopup itemFrameChooserPopup = new ItemFrameChooserPopup();
	private FrameEditor frameEditor = new FrameEditor();

	public ItemEditor() {
		super("itemType");

		// Contruction
		ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
		itemFrameChooserPopup.addInteractionListener(this);
		CPEButton frameChooser = new CPEButton("generic.select");
		frameChooser.addAction(() -> {
			if (ResourcesService.getInstance().containsResource(getValue().getImage())) {
				itemFrameChooserPopup.show(frameChooser, getValue().getImage());
			}
		});
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
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.frame", LayoutUtils.createHorizontalBox(frameEditor, frameChooser), gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "itemEditor.maxStackSize", maxStackSizeInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", getTypeChooser(), gbc);

		getSpecificPartPanel().setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));
		LayoutUtils.addVertically(this, northPanel, getSpecificPartPanel());

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		super.valueChanged(source);
		itemPreview.setImage(getValue().getImage());
		itemPreview.setFrame(getValue().getFrame());
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
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

	@Override
	public void interactionPerformed(Object data) {
		ClickEvent clickEvent = (ClickEvent) data;
		getValue().getFrame().setX(clickEvent.getSpriteX());
		getValue().getFrame().setY(clickEvent.getSpriteY());
		frameEditor.setValue(getValue().getFrame());
		frameEditor.notifyValueChanged();
		itemFrameChooserPopup.setVisible(false);
	}
}
