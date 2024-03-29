package com.pixurvival.contentPackEditor.component.behaviorSet;

import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;

import java.awt.*;
import java.util.Collections;

public class BehaviorSetEditor extends RootElementEditor<BehaviorSet> {

    private static final long serialVersionUID = 1L;

    private ListEditor<Behavior> behaviorEditors = new VerticalListEditor<>(() -> new BehaviorEditor(() -> {
        if (getValue() == null) {
            return Collections.emptyList();
        } else {
            return getValue().getBehaviors();
        }
    }), MoveTowardBehavior::new);

    public BehaviorSetEditor() {
        super(BehaviorSet.class);

        bind(behaviorEditors, "behaviors");

        setLayout(new BorderLayout());
        add(behaviorEditors, BorderLayout.CENTER);
    }

    @Override
    public boolean isValueValid(BehaviorSet value) {
        if (value == null) {
            return false;
        }
        if (getValue() == value) {
            return super.isValueValid(value);
        }
        BehaviorSet previousValue = getValue();
        setValue(value);
        boolean result = super.isValueValid(value);
        setValue(previousValue);
        return result;
    }
}
