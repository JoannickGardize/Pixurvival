package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.gameMode.DayCycle;
import com.pixurvival.core.contentPack.gameMode.DayNightCycle;
import com.pixurvival.core.contentPack.gameMode.EternalDayCycle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DayCycleEditor extends InstanceChangingElementEditor<DayCycle> {

    private static final long serialVersionUID = 1L;

    public DayCycleEditor() {
        super(DayCycle.class, "dayCycleType", null);
        LayoutUtils.addHorizontally(this, getTypeChooser(), getSpecificPartPanel());
    }

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> classEntries = new ArrayList<>();
        classEntries.add(new ClassEntry(EternalDayCycle.class, JPanel::new));
        classEntries.add(new ClassEntry(DayNightCycle.class, () -> {
            TimeInput dayDurationInput = new TimeInput();
            TimeInput nightDurationInput = new TimeInput();
            bind(dayDurationInput, "dayDuration", DayNightCycle.class);
            bind(nightDurationInput, "nightDuration", DayNightCycle.class);
            return LayoutUtils.single(LayoutUtils.createHorizontalLabelledBox("dayNightCycle.dayDuration", dayDurationInput, "dayNightCycle.nightDuration", nightDurationInput));
        }));
        return classEntries;
    }

    @Override
    protected void initialize(DayCycle oldInstance, DayCycle newInstance) {

    }

}
