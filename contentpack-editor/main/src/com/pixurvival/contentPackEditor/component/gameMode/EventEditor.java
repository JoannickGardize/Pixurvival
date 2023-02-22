package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.gameMode.event.EffectEvent;
import com.pixurvival.core.contentPack.gameMode.event.Event;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventEditor extends InstanceChangingElementEditor<Event> {

    public EventEditor() {
        super(Event.class, "eventType", null);

        TimeInput startTimeInput = new TimeInput();
        TimeInput repeatTimeInput = new TimeInput();

        bind(startTimeInput, "startTime");
        bind(repeatTimeInput, "repeatTime");

        setLayout(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
        add(LayoutUtils.createHorizontalLabelledBox("generic.type", getTypeChooser(), "eventEditor.startTime", startTimeInput, "eventEditor.repeat", repeatTimeInput), BorderLayout.NORTH);
        add(getSpecificPartPanel(), BorderLayout.CENTER);
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();

        entries.add(new ClassEntry(EffectEvent.class, () -> {
            ElementChooserButton<Effect> effectChooser = new ElementChooserButton<>(Effect.class);
            BooleanCheckBox forEachTeamCheckBox = new BooleanCheckBox();
            IntegerInput maximumRepeatValueInput = new IntegerInput();
            EventPositionEditor eventPositionEditor = new EventPositionEditor();
            bind(effectChooser, "effect", EffectEvent.class);
            bind(forEachTeamCheckBox, "forEachTeam", EffectEvent.class);
            bind(eventPositionEditor, "position", EffectEvent.class);
            bind(maximumRepeatValueInput, "maximumRepeatValue", EffectEvent.class);
            JPanel panel = new JPanel(new BorderLayout(LayoutUtils.DEFAULT_GAP, LayoutUtils.DEFAULT_GAP));
            panel.add(LayoutUtils.createVerticalBox(
                    LayoutUtils.createHorizontalLabelledBox("elementType.effect", LayoutUtils.single(effectChooser), "eventEditor.forEachTeam", forEachTeamCheckBox, "eventEditor.maximumRepeatValue",
                            LayoutUtils.single(maximumRepeatValueInput)),
                    new JLabel("Strength = number of player"), new JLabel("Agility = current number of repeat (start from 1)"), new JLabel("Intelligence = Strength x Agility"),
                    new JLabel("This will change for parameterizable purpose in the future")), BorderLayout.NORTH);
            eventPositionEditor.setBorder(LayoutUtils.createGroupBorder("generic.position"));
            panel.add(eventPositionEditor, BorderLayout.CENTER);
            return panel;
        }));

        return entries;
    }

    @Override
    protected void initialize(Event oldInstance, Event newInstance) {
        newInstance.setStartTime(oldInstance.getStartTime());
        newInstance.setRepeatTime(oldInstance.getRepeatTime());
    }

}
