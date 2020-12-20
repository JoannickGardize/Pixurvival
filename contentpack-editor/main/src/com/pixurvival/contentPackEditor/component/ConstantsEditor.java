package com.pixurvival.contentPackEditor.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementAttribute;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
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

	private ElementChooserButton<SpriteSheet> defaultCharacterChooser = new ElementChooserButton<>(SpriteSheet.class);
	private ElementChooserButton<Tile> outsideTileChooser = new ElementChooserButton<>(Tile.class);

	public ConstantsEditor() {
		super(Constants.class);
		EventManager.getInstance().register(this);
		defaultCharacterChooser.getSearchPopup().setModal(true);
		outsideTileChooser.getSearchPopup().setModal(true);
		// Construction
		TimeInput tileAnimationSpeedInput = new TimeInput();

		// Binding
		bind(defaultCharacterChooser, "defaultCharacter");
		bind(outsideTileChooser, "outsideTile");
		bind(tileAnimationSpeedInput, "tileAnimationSpeed");

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "constants.defaultCharacter", defaultCharacterChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "constants.outsideTile", outsideTileChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "constants.tileAnimationSpeed", tileAnimationSpeedInput, gbc);
	}

	@EventListener
	public void contentPackloaded(ContentPackLoadedEvent event) {
		setValue(event.getContentPack().getConstants());
	}

	@Override
	public <T> ElementAttribute<Constants, T> bind(ValueComponent<T> component, String attributeName) {
		component.addValueChangeListener(v -> EventManager.getInstance().fire(new ContentPackConstantChangedEvent(getValue())));
		return super.bind(component, attributeName);
	}
}
