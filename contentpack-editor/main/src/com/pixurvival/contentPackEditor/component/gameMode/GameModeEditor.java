package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerIntervalEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.map.MapGenerator;

public class GameModeEditor extends RootElementEditor<GameMode> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Ecosystem> ecosystemChooser = new ElementChooserButton<>(Ecosystem.class);
	private ElementChooserButton<MapGenerator> mapGeneratorChooser = new ElementChooserButton<>(MapGenerator.class);

	public GameModeEditor() {

		IntegerIntervalEditor teamNumberInterval = new IntegerIntervalEditor("gameMode.teamNumber");
		IntegerIntervalEditor teamSizeInterval = new IntegerIntervalEditor("gameMode.teamSize");
		DayCycleEditor dayCycleDefinitionEditor = new DayCycleEditor();
		FloatInput spawnSquareSizeInput = new FloatInput(Bounds.positive());
		BooleanCheckBox mapLimitEnabledInput = new BooleanCheckBox();
		FloatInput mapLimitSizeInput = new FloatInput(Bounds.positive());
		ListEditor<Event> eventsEditor = new VerticalListEditor<>(LayoutUtils.bordered(EventEditor::new), EffectEvent::new);
		EndGameConditionEditor endGameConditionEditor = new EndGameConditionEditor();

		// Binding

		bind(teamNumberInterval, GameMode::getTeamNumberInterval, GameMode::setTeamNumberInterval);
		bind(teamSizeInterval, GameMode::getTeamSizeInterval, GameMode::setTeamSizeInterval);
		bind(dayCycleDefinitionEditor, GameMode::getDayCycle, GameMode::setDayCycle);
		bind(ecosystemChooser, GameMode::getEcosystem, GameMode::setEcosystem);
		bind(mapGeneratorChooser, GameMode::getMapGenerator, GameMode::setMapGenerator);
		bind(spawnSquareSizeInput, GameMode::getSpawnSquareSize, GameMode::setSpawnSquareSize);
		bind(mapLimitEnabledInput, GameMode::isMapLimitEnabled, GameMode::setMapLimitEnabled);
		bind(mapLimitSizeInput, GameMode::getMapLimitSize, GameMode::setMapLimitSize);
		bind(eventsEditor, GameMode::getEvents, GameMode::setEvents);
		bind(endGameConditionEditor, GameMode::getEndGameCondition, GameMode::setEndGameCondition);

		// Layouting

		dayCycleDefinitionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.dayCycle"));
		endGameConditionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.endGameCondition"));
		Container teamPanel = LayoutUtils.sideBySide(teamNumberInterval, teamSizeInterval);
		JPanel elementLinksPanel = LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapGenerator", mapGeneratorChooser);
		JPanel mapConfigPanel = LayoutUtils.createHorizontalLabelledBox("gameMode.spawnSquareSize", spawnSquareSizeInput, "gameMode.mapLimitEnabled", mapLimitEnabledInput, "gameMode.mapLimitSize",
				mapLimitSizeInput);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TranslationService.getInstance().getString("generic.general"),
				LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 5, teamPanel, dayCycleDefinitionEditor, endGameConditionEditor, elementLinksPanel, mapConfigPanel, new JPanel()));
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.events"), eventsEditor);

		LayoutUtils.fill(this, tabbedPane);
	}
}
