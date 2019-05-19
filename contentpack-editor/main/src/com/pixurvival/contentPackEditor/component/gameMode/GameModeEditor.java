package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.map.MapGenerator;

public class GameModeEditor extends RootElementEditor<GameMode> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Ecosystem> ecosystemChooser = new ElementChooserButton<>();
	private ElementChooserButton<MapGenerator> mapGeneratorChooser = new ElementChooserButton<>();

	public GameModeEditor() {

		// Binding

		bind(ecosystemChooser, GameMode::getEcosystem, GameMode::setEcosystem);
		bind(mapGeneratorChooser, GameMode::getMapGenerator, GameMode::setMapGenerator);

		// Layouting

		add(LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapGenerator", mapGeneratorChooser));
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		ecosystemChooser.setItems(event.getContentPack().getEcosystems());
		mapGeneratorChooser.setItems(event.getContentPack().getMapGenerators());
	}
}
