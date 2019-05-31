package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.Constants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class ConstantsEditor extends ElementEditor<Constants> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> defaultCharacterChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());
	private ElementChooserButton<Tile> outsideTileChooser = new ElementChooserButton<>(IconService.getInstance()::get);

	public ConstantsEditor() {
		EventManager.getInstance().register(this);
		defaultCharacterChooser.getSearchPopup().setModal(true);
		outsideTileChooser.getSearchPopup().setModal(true);
		// Construction
		DoubleInput tileAnimationSpeedInput = new DoubleInput(Bounds.positive());

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

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		setValue(event.getContentPack().getConstants());
		defaultCharacterChooser.setItems(event.getContentPack().getSpriteSheets());
		outsideTileChooser.setItems(event.getContentPack().getTiles());
	}

	@Override
	public <T> void bind(ValueComponent<T> component, Function<Constants, T> getter, BiConsumer<Constants, T> setter) {
		component.addValueChangeListener(v -> EventManager.getInstance().fire(new ContentPackConstantChangedEvent(getValue())));
		super.bind(component, getter, setter);
	}
}
