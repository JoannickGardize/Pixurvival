package com.pixurvival.contentPackEditor.component.item;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NumberInput;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item.Equipable;

public class EquipableEditor<T extends Equipable> extends ElementEditor<T> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());

	public EquipableEditor() {
		EventManager.getInstance().register(this);

		// Construction

		NumberInput<Double> agilityInput = NumberInput.doubleInput(Bounds.minBounds(0));
		NumberInput<Double> strengthInput = NumberInput.doubleInput(Bounds.minBounds(0));
		NumberInput<Double> intelligenceInput = NumberInput.doubleInput(Bounds.minBounds(0));

		// Binding

		bind(spriteSheetChooser, Equipable::getSpriteSheet, Equipable::setSpriteSheet);
		bind(agilityInput, e -> (double) e.getAgilityBonus(), (e, v) -> e.setAgilityBonus(v.floatValue()));
		bind(strengthInput, e -> (double) e.getStrengthBonus(), (e, v) -> e.setStrengthBonus(v.floatValue()));
		bind(intelligenceInput, e -> (double) e.getIntelligenceBonus(), (e, v) -> e.setIntelligenceBonus(v.floatValue()));

		// Layouting

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "itemEditor.equipable.agilityBonus", agilityInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "itemEditor.equipable.strengthBonus", strengthInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "itemEditor.equipable.intelligenceBonus", intelligenceInput, gbc);

	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

}
