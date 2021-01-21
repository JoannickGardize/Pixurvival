package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.Vector2Editor;
import com.pixurvival.core.Direction;
import com.pixurvival.core.contentPack.gameMode.event.EventPosition;
import com.pixurvival.core.contentPack.gameMode.event.PlayerProximityEventPosition;
import com.pixurvival.core.contentPack.gameMode.event.RandomRectangeEventPosition;
import com.pixurvival.core.contentPack.gameMode.event.StaticEventPosition;

public class EventPositionEditor extends InstanceChangingElementEditor<EventPosition> {

	private static final long serialVersionUID = 1L;

	public EventPositionEditor() {
		super(EventPosition.class, "eventPositionType", null);

		setLayout(new BorderLayout());
		add(LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())), BorderLayout.WEST);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();
		entries.add(new ClassEntry(PlayerProximityEventPosition.class, () -> {
			FloatInput distanceInput = new FloatInput();
			bind(distanceInput, "distance", PlayerProximityEventPosition.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.distance", distanceInput));
		}));

		entries.add(new ClassEntry(StaticEventPosition.class, () -> {
			Vector2Editor positionEditor = new Vector2Editor();
			bind(positionEditor, "position", StaticEventPosition.class);
			return LayoutUtils.single(LayoutUtils.labelled("generic.position", positionEditor));
		}));

		entries.add(new ClassEntry(RandomRectangeEventPosition.class, () -> {
			FloatInput xInput = new FloatInput();
			FloatInput yInput = new FloatInput();
			FloatInput widthInput = new FloatInput();
			FloatInput heightInput = new FloatInput();
			EnumChooser<Direction> targetDirectionChooser = new EnumChooser<>(Direction.class);

			bind(xInput, "x", RandomRectangeEventPosition.class);
			bind(yInput, "y", RandomRectangeEventPosition.class);
			bind(widthInput, "width", RandomRectangeEventPosition.class);
			bind(heightInput, "height", RandomRectangeEventPosition.class);
			bind(targetDirectionChooser, "targetDirection", RandomRectangeEventPosition.class);

			return LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("generic.x", xInput, "generic.y", yInput, "generic.width", widthInput, "generic.height", heightInput),
					LayoutUtils.single(LayoutUtils.labelled("gameMode.targetDirection", targetDirectionChooser)));
		}));

		return entries;
	}

	@Override
	protected void initialize(EventPosition oldInstance, EventPosition newInstance) {
	}

}
