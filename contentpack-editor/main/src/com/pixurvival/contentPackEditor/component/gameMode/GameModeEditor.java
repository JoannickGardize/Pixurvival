package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerIntervalEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.PlayerDeathItemHandling;
import com.pixurvival.core.contentPack.gameMode.PlayerRespawnType;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamEndCondition;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.map.MapProvider;

public class GameModeEditor extends RootElementEditor<GameMode> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<Ecosystem> ecosystemChooser = new ElementChooserButton<>(Ecosystem.class);
	private ElementChooserButton<MapProvider> mapGeneratorChooser = new ElementChooserButton<>(MapProvider.class);
	private TimeInput playerRespawnDelayInput = new TimeInput();
	private BooleanCheckBox keepPermanentStatsCheckBox = new BooleanCheckBox();

	public GameModeEditor() {
		super(GameMode.class);

		FloatInput hungerPerMinuteInput = new FloatInput();
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
		EnumChooser<PlayerDeathItemHandling> playerDeathItemHandlingChooser = new EnumChooser<>(PlayerDeathItemHandling.class);
		EnumChooser<PlayerRespawnType> playerRespawnTypeChooser = new EnumChooser<>(PlayerRespawnType.class);

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
		bind(playerDeathItemHandlingChooser, "playerDeathItemHandling");
		bind(playerRespawnTypeChooser, "playerRespawnType");
		bind(playerRespawnDelayInput, "playerRespawnDelay");
		bind(keepPermanentStatsCheckBox, "keepPermanentStats");
		bind(hungerPerMinuteInput, "hungerPerMinute");

		// Layouting

		dayCycleDefinitionEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.dayCycle"));
		endGameConditionsEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.endGameCondition"));
		Container teamPanel = LayoutUtils.sideBySide(teamNumberInterval, teamSizeInterval);
		JPanel elementLinksPanel = LayoutUtils.createHorizontalLabelledBox("elementType.ecosystem", ecosystemChooser, "elementType.mapProvider", mapGeneratorChooser);
		playerSpawnEditor.setBorder(LayoutUtils.createGroupBorder("gameMode.playerSpawn"));

		JPanel deathPanel = new JPanel(new GridBagLayout());
		deathPanel.setBorder(LayoutUtils.createGroupBorder("gameMode.deathHandling"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(deathPanel, "gameMode.playerDeathItemHandling", playerDeathItemHandlingChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(deathPanel, "gameMode.playerRespawnType", playerRespawnTypeChooser, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(deathPanel, "gameMode.keepPermanentStats", keepPermanentStatsCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(deathPanel, "gameMode.playerRespawnDelay", playerRespawnDelayInput, gbc);

		JPanel allPanel = new JPanel();
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		allPanel.add(teamPanel);
		allPanel.add(LayoutUtils.single(LayoutUtils.labelled("gameMode.hungerPerMinute", hungerPerMinuteInput)));
		allPanel.add(dayCycleDefinitionEditor);
		allPanel.add(endGameConditionsEditor);
		allPanel.add(LayoutUtils.single(elementLinksPanel));
		allPanel.add(playerSpawnEditor);
		allPanel.add(deathPanel);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TranslationService.getInstance().getString("generic.general"), allPanel);
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

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (getValue() != null) {
			playerRespawnDelayInput.setEnabled(getValue().getPlayerRespawnType() != PlayerRespawnType.NONE);
			keepPermanentStatsCheckBox.setEnabled(getValue().getPlayerRespawnType() != PlayerRespawnType.NONE);
		}
		super.valueChanged(source);
	}
}
