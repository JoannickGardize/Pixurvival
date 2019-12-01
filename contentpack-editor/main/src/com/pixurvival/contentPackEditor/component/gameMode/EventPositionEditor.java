package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.Vector2Editor;
import com.pixurvival.core.contentPack.gameMode.event.EventPosition;
import com.pixurvival.core.contentPack.gameMode.event.PlayerProximityEventPosition;
import com.pixurvival.core.contentPack.gameMode.event.StaticEventPosition;

public class EventPositionEditor extends InstanceChangingElementEditor<EventPosition> {

	private static final long serialVersionUID = 1L;

	public EventPositionEditor() {
		super("eventPositionType", null);

		setLayout(new BorderLayout());
		add(LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())), BorderLayout.WEST);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		FloatInput distanceInput = new FloatInput(Bounds.positive());
		bind(distanceInput, PlayerProximityEventPosition::getDistance, PlayerProximityEventPosition::setDistance, PlayerProximityEventPosition.class);
		entries.add(new ClassEntry(PlayerProximityEventPosition.class, LayoutUtils.single(LayoutUtils.labelled("generic.distance", distanceInput))));

		Vector2Editor positionEditor = new Vector2Editor();
		bind(positionEditor, StaticEventPosition::getPosition, StaticEventPosition::setPosition, StaticEventPosition.class);
		entries.add(new ClassEntry(StaticEventPosition.class, LayoutUtils.single(LayoutUtils.labelled("generic.position", positionEditor))));

		return entries;
	}

	@Override
	protected void initialize(EventPosition oldInstance, EventPosition newInstance) {
	}

}
