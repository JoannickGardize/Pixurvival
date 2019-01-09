package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NumberInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.Constants;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class ConstantsEditor extends ElementEditor<Constants> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> defaultCharacterChooser = new ElementChooserButton<>(
			LayoutUtils.getSpriteSheetIconProvider());

	public ConstantsEditor() {
		EventManager.getInstance().register(this);
		defaultCharacterChooser.getSearchPopup().setModal(true);
		// Construction
		NumberInput<Double> tileAnimationSpeedInput = NumberInput.doubleInput(Bounds.positive());

		// Binding
		bind(defaultCharacterChooser, Constants::getDefaultCharacter, Constants::setDefaultCharacter);
		bind(tileAnimationSpeedInput, Constants::getTileAnimationSpeed, Constants::setTileAnimationSpeed);

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "constants.defaultCharacter", defaultCharacterChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "constants.tileAnimationSpeed", tileAnimationSpeedInput, gbc);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		setValue(event.getContentPack().getConstants());
		defaultCharacterChooser.setItems(event.getContentPack().getSpriteSheets());
	}

	@Override
	protected <T> void bind(ValueComponent<T> component, Function<Constants, T> getter,
			BiConsumer<Constants, T> setter) {
		component.addValueChangeListener(
				v -> EventManager.getInstance().fire(new ContentPackConstantChangedEvent(getValue())));
		super.bind(component, getter, setter);
	}
}
