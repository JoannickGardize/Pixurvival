package com.pixurvival.contentPackEditor.component.gameMode;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.gameMode.DayCycle;
import com.pixurvival.core.contentPack.gameMode.DayNightCycle;
import com.pixurvival.core.contentPack.gameMode.EternalDayCycle;

public class DayCycleEditor extends InstanceChangingElementEditor<DayCycle> {

	private static final long serialVersionUID = 1L;

	public DayCycleEditor() {
		super("dayCycleType");
		LayoutUtils.addHorizontally(this, getTypeChooser(), getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> classEntries = new ArrayList<>();
		classEntries.add(new ClassEntry(EternalDayCycle.class, new JPanel()));
		TimeInput dayDurationInput = new TimeInput();
		TimeInput nightDurationInput = new TimeInput();
		bind(dayDurationInput, DayNightCycle::getDayDuration, DayNightCycle::setDayDuration, DayNightCycle.class);
		bind(nightDurationInput, DayNightCycle::getNightDuration, DayNightCycle::setNightDuration, DayNightCycle.class);
		classEntries
				.add(new ClassEntry(DayNightCycle.class, LayoutUtils.createHorizontalLabelledBox("dayNightCycle.dayDuration", dayDurationInput, "dayNightCycle.nightDuration", nightDurationInput)));
		return classEntries;
	}

	@Override
	protected void initialize(DayCycle oldInstance, DayCycle newInstance) {

	}

}
