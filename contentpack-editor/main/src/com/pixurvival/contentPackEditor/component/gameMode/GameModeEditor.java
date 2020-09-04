package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerIntervalEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;

public class GameModeEditor extends RootElementEditor<GameMode> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Ecosystem> ecosystemChooser = new ElementChooserButton<>(Ecosystem.class);
	private ElementChooserButton<ProcedurallyGeneratedMapProvider> mapGeneratorChooser = new ElementChooserButton<>(ProcedurallyGeneratedMapProvider.class);

	public GameModeEditor() {

		IntegerIntervalEditor teamNumberInterval = new IntegerIntervalEditor("gameMode.teamNumber");
		IntegerIntervalEditor teamSizeInterval = new IntegerIntervalEditor("gameMode.teamSize");
		DayCycleEditor dayCycleDefinitionEditor = new DayCycleEditor();
		PlayerSpawnEditor playerSpawnEditor = new PlayerSpawnEditor();
		ListEditor<Event> eventsEditor = new VerticalListEditor<>(LayoutUtils.bordered(EventEditor::new), EffectEvent::new);
		EndGameConditionEditor endGameConditionEditor = new EndGameConditionEditor();
		MapLimitsEditor mapLimitsEditor = new MapLimitsEditor();

		// Binding

		bind(teamNumberInterval, GameMode::getTeamNumberInterval, GameMode::setTeamNumberInterval);
		bind(teamSizeInterval, GameMode::getTeamSizeInterval, GameMode::setTeamSizeInterval);
		bind(dayCycleDefinitionEditor, GameMode::getDayCycle, GameMode::setDayCycle);
		bind(ecosystemChooser, GameMode::getEcosystem, GameMode::setEcosystem);
		bind(mapGeneratorChooser, GameMode::getMapProvider, GameMode::setMapProvider);
		bind(playerSpawnEditor, GameMode::getPlayerSpawn, GameMode::setPlayerSpawn);
		bind(eventsEditor, GameMode::getEvents, GameMode::setEvents);
		bind(endGameConditionEditor, GameMode::getEndGameCondition, GameMode::setEndGameCondition);
		bind(mapLimitsEditor, GameMode::getMapLimits, GameMode::setMapLimits);

		// Layouting

		dayCycleDefinitionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.dayCycle"));
		endGameConditionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.endGameCondition"));
		Container teamPanel = LayoutUtils.sideBySide(teamNumberInterval, teamSizeInterval);
		JPanel elementLinksPanel = LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapProvider", mapGeneratorChooser);
		playerSpawnEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.playerSpawn"));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TranslationService.getInstance().getString("generic.general"),
				LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 4, teamPanel, dayCycleDefinitionEditor, endGameConditionEditor, elementLinksPanel, playerSpawnEditor));
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.events"), eventsEditor);
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.mapLimits"), mapLimitsEditor);

		LayoutUtils.fill(this, tabbedPane);
	}
}
