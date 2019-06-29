package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.Container;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerIntervalEditor;
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

		IntegerIntervalEditor teamNumberInterval = new IntegerIntervalEditor("gameMode.teamNumber");
		IntegerIntervalEditor teamSizeInterval = new IntegerIntervalEditor("gameMode.teamSize");
		DayCycleEditor dayCycleDefinitionEditor = new DayCycleEditor();
		DoubleInput spawnSquareSizeInput = new DoubleInput(Bounds.positive());
		BooleanCheckBox mapLimitEnabledInput = new BooleanCheckBox();
		DoubleInput mapLimitSizeInput = new DoubleInput(Bounds.positive());

		// Binding

		bind(teamNumberInterval, GameMode::getTeamNumberInterval, GameMode::setTeamNumberInterval);
		bind(teamSizeInterval, GameMode::getTeamSizeInterval, GameMode::setTeamSizeInterval);
		bind(dayCycleDefinitionEditor, GameMode::getDayCycle, GameMode::setDayCycle);
		bind(ecosystemChooser, GameMode::getEcosystem, GameMode::setEcosystem);
		bind(mapGeneratorChooser, GameMode::getMapGenerator, GameMode::setMapGenerator);
		bind(spawnSquareSizeInput, GameMode::getSpawnSquareSize, GameMode::setSpawnSquareSize);
		bind(mapLimitEnabledInput, GameMode::isMapLimitEnabled, GameMode::setMapLimitEnabled);
		bind(mapLimitSizeInput, GameMode::getMapLimitSize, GameMode::setMapLimitSize);

		// Layouting

		dayCycleDefinitionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.dayCycle"));
		Container teamPanel = LayoutUtils.sideBySide(teamNumberInterval, teamSizeInterval);
		JPanel elementLinksPanel = LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapGenerator", mapGeneratorChooser);
		JPanel mapConfigPanel = LayoutUtils.createHorizontalLabelledBox("gameMode.spawnSquareSize", spawnSquareSizeInput, "gameMode.mapLimitEnabled", mapLimitEnabledInput, "gameMode.mapLimitSize",
				mapLimitSizeInput);

		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 4, teamPanel, dayCycleDefinitionEditor, elementLinksPanel, mapConfigPanel, new JPanel());
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		ecosystemChooser.setItems(event.getContentPack().getEcosystems());
		mapGeneratorChooser.setItems(event.getContentPack().getMapGenerators());
	}
}
