package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.BeanFactory;
import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.contentPack.trigger.EquippedTrigger;
import com.pixurvival.core.contentPack.trigger.TimerTrigger;
import com.pixurvival.core.contentPack.trigger.Trigger;
import com.pixurvival.core.contentPack.trigger.UnequippedTrigger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TriggerEditor extends InstanceChangingElementEditor<Trigger> {
    protected TriggerEditor() {
        super(Trigger.class, "triggerType");
        ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(AlterationEditor::new, BeanFactory.of(Alteration.class), VerticalListEditor.HORIZONTAL, false);

        bind(alterationsEditor, "alterations");

        alterationsEditor.setBorder(LayoutUtils.createGroupBorder("alterationAbilityEditor.selfAlterations"));

        LayoutUtils.addVertically(this,
                LayoutUtils.single(LayoutUtils.labelled("generic.type", getTypeChooser())),
                LayoutUtils.createHorizontalBox(1, getSpecificPartPanel(), alterationsEditor));
    }

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();

        entries.add(new ClassEntry(EquippedTrigger.class, () -> new JPanel()));

        entries.add(new ClassEntry(UnequippedTrigger.class, () -> new JPanel()));

        entries.add(new ClassEntry(TimerTrigger.class, () -> {
            TimeInput startDelayInput = new TimeInput();
            TimeInput intervalInput = new TimeInput();

            bind(startDelayInput, "startDelay", TimerTrigger.class);
            bind(intervalInput, "interval", TimerTrigger.class);

            return LayoutUtils.createVerticalLabelledBox("timerTriggerEditor.startDelay", startDelayInput, "generic.interval", intervalInput);
        }));

        return entries;
    }

    @Override
    protected void initialize(Trigger oldInstance, Trigger newInstance) {
        newInstance.setAlterations(oldInstance.getAlterations());
    }
}
