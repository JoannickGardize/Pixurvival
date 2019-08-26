package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.gameMode.event.Event;

public class EventEditor extends InstanceChangingElementEditor<Event> {

	public EventEditor() {
		super("eventType");

		TimeInput timeInput = new TimeInput();
		BooleanCheckBox repeatCheckBox = new BooleanCheckBox();

		bind(timeInput, Event::getTime, Event::setTime);
		bind(repeatCheckBox, Event::isRepeat, Event::setRepeat);

		setLayout(new BorderLayout());
		add(LayoutUtils.createHorizontalLabelledBox("generic.type", getTypeChooser(), "generic.time", timeInput, "generic.repeat", repeatCheckBox), BorderLayout.NORTH);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected List<ClassEntry> getClassEntries() {

		return null;
	}

	@Override
	protected void initialize(Event oldInstance, Event newInstance) {
		newInstance.setTime(oldInstance.getTime());
		newInstance.setRepeat(oldInstance.isRepeat());
	}

}
