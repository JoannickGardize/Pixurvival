package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;

public class EventEditor extends InstanceChangingElementEditor<Event> {

	public EventEditor() {
		super("eventType");

		TimeInput timeInput = new TimeInput();
		BooleanCheckBox repeatCheckBox = new BooleanCheckBox();

		bind(timeInput, Event::getTime, Event::setTime);
		bind(repeatCheckBox, Event::isRepeat, Event::setRepeat);

		setLayout(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
		add(LayoutUtils.createHorizontalLabelledBox("generic.type", getTypeChooser(), "generic.time", timeInput, "generic.repeat", repeatCheckBox), BorderLayout.NORTH);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		ElementChooserButton<Effect> effectChooser = new ElementChooserButton<>(Effect.class);
		BooleanCheckBox forEachTeamCheckBox = new BooleanCheckBox();
		EventPositionEditor eventPositionEditor = new EventPositionEditor();
		bind(effectChooser, EffectEvent::getEffect, EffectEvent::setEffect, EffectEvent.class);
		bind(forEachTeamCheckBox, EffectEvent::isForEachTeam, EffectEvent::setForEachTeam, EffectEvent.class);
		bind(eventPositionEditor, EffectEvent::getPosition, EffectEvent::setPosition, EffectEvent.class);
		JPanel panel = new JPanel(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
		panel.add(LayoutUtils.createHorizontalLabelledBox("elementType.effect", effectChooser, "eventEditor.forEachTeam", forEachTeamCheckBox), BorderLayout.NORTH);
		eventPositionEditor.setBorder(LayoutUtils.createGroupBorder("generic.position"));
		panel.add(eventPositionEditor, BorderLayout.CENTER);
		entries.add(new ClassEntry(EffectEvent.class, panel));

		return entries;
	}

	@Override
	protected void initialize(Event oldInstance, Event newInstance) {
		newInstance.setTime(oldInstance.getTime());
		newInstance.setRepeat(oldInstance.isRepeat());
	}

}
