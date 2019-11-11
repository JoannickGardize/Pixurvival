package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.Constants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class ConstantsEditor extends ElementEditor<Constants> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> defaultCharacterChooser = new ElementChooserButton<>(SpriteSheet.class);
	private ElementChooserButton<Tile> outsideTileChooser = new ElementChooserButton<>(Tile.class);

	public ConstantsEditor() {
		defaultCharacterChooser.getSearchPopup().setModal(true);
		outsideTileChooser.getSearchPopup().setModal(true);
		// Construction
		TimeInput tileAnimationSpeedInput = new TimeInput();

		// Binding
		bind(defaultCharacterChooser, Constants::getDefaultCharacter, Constants::setDefaultCharacter);
		bind(outsideTileChooser, Constants::getOutsideTile, Constants::setOutsideTile);
		bind(tileAnimationSpeedInput, Constants::getTileAnimationSpeed, Constants::setTileAnimationSpeed);

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "constants.defaultCharacter", defaultCharacterChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "constants.outsideTile", outsideTileChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "constants.tileAnimationSpeed", tileAnimationSpeedInput, gbc);
	}

	@Override
	public <T> void bind(ValueComponent<? extends T> component, Function<Constants, T> getter, BiConsumer<Constants, T> setter) {
		component.addValueChangeListener(v -> EventManager.getInstance().fire(new ContentPackConstantChangedEvent(getValue())));
		super.bind(component, getter, setter);
	}
}
