package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.Container;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerIntervalEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamEndCondition;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.map.MapProvider;

public class GameModeEditor extends RootElementEditor<GameMode> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Ecosystem> ecosystemChooser = new ElementChooserButton<>(Ecosystem.class);
	private ElementChooserButton<MapProvider> mapGeneratorChooser = new ElementChooserButton<>(MapProvider.class);

	public GameModeEditor() {
		super(GameMode.class);

		IntegerIntervalEditor teamNumberInterval = new IntegerIntervalEditor("gameMode.teamNumber");
		IntegerIntervalEditor teamSizeInterval = new IntegerIntervalEditor("gameMode.teamSize");
		DayCycleEditor dayCycleDefinitionEditor = new DayCycleEditor();
		PlayerSpawnEditor playerSpawnEditor = new PlayerSpawnEditor();
		ListEditor<Event> eventsEditor = new VerticalListEditor<>(LayoutUtils.bordered(EventEditor::new), () -> BeanFactory.newInstance(EffectEvent.class));
		ListEditor<EndGameCondition> endGameConditionsEditor = new VerticalListEditor<>(() -> new EndGameConditionEditor(() -> {
			if (getValue() == null || getValue().getRoles() == null) {
				return Collections.emptyList();
			} else {
				return getValue().getRoles().getRoles();
			}
		}), RemainingTeamEndCondition::new);
		MapLimitsEditor mapLimitsEditor = new MapLimitsEditor();
		RolesEditor rolesEditor = new RolesEditor();

		// Binding

		bind(teamNumberInterval, "teamNumberInterval");
		bind(teamSizeInterval, "teamSizeInterval");
		bind(dayCycleDefinitionEditor, "dayCycle");
		bind(ecosystemChooser, "ecosystem");
		bind(mapGeneratorChooser, "mapProvider");
		bind(playerSpawnEditor, "playerSpawn");
		bind(eventsEditor, "events");
		bind(endGameConditionsEditor, "endGameConditions");
		bind(mapLimitsEditor, "mapLimits");
		bind(rolesEditor, "roles");

		// Layouting

		dayCycleDefinitionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.dayCycle"));
		endGameConditionsEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.endGameCondition"));
		Container teamPanel = LayoutUtils.sideBySide(teamNumberInterval, teamSizeInterval);
		JPanel elementLinksPanel = LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapProvider", mapGeneratorChooser);
		playerSpawnEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.playerSpawn"));

		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		generalPanel.add(teamPanel);
		generalPanel.add(dayCycleDefinitionEditor);
		generalPanel.add(endGameConditionsEditor);
		generalPanel.add(LayoutUtils.single(elementLinksPanel));
		generalPanel.add(playerSpawnEditor);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TranslationService.getInstance().getString("generic.general"), generalPanel);
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.events"), eventsEditor);
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.mapLimits"), mapLimitsEditor);
		tabbedPane.addTab(TranslationService.getInstance().getString("gameMode.roles"), rolesEditor);

		LayoutUtils.fill(this, tabbedPane);
	}

	@Override
	public boolean isValueValid(GameMode value) {
		if (value == null) {
			return false;
		}
		if (value.getRoles() == null || getValue() == value) {
			return super.isValueValid(value);
		}
		// Solves roles reference problem
		GameMode previousValue = getValue();
		setValue(value, true);
		boolean result = super.isValueValid(value);
		setValue(previousValue, true);
		return result;
	}
}
