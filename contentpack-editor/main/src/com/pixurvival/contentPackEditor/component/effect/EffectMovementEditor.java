package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.contentPack.effect.*;

import java.util.ArrayList;
import java.util.List;

public class EffectMovementEditor extends InstanceChangingElementEditor<EffectMovement> {

    private static final long serialVersionUID = 1L;

    public EffectMovementEditor() {
        super(EffectMovement.class, "effectMovementType", null);
        LayoutUtils.addHorizontally(this, 1, LayoutUtils.labelled("generic.type", getTypeChooser()), getSpecificPartPanel());
    }

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> list = new ArrayList<>();

        // StaticEffectMovement
        list.add(new ClassEntry(StaticEffectMovement.class, () -> {
            FloatInput minDistanceInput = new FloatInput();
            FloatInput maxDistanceInput = new FloatInput();
            bind(minDistanceInput, "minDistance", StaticEffectMovement.class);
            bind(maxDistanceInput, "maxDistance", StaticEffectMovement.class);
            return LayoutUtils.createHorizontalLabelledBox("generic.minDistance", minDistanceInput, "generic.maxDistance", maxDistanceInput);
        }));

        // BoundEffectMovement
        list.add(new ClassEntry(BoundEffectMovement.class, () -> {
            FloatInput distanceInput = new FloatInput();
            FloatInput randomDistanceInput = new FloatInput();
            bind(distanceInput, "distance", BoundEffectMovement.class);
            bind(randomDistanceInput, "randomDistance", BoundEffectMovement.class);
            return LayoutUtils.createHorizontalLabelledBox("generic.distance", distanceInput, "effectMovementEditor.randomDistance", randomDistanceInput);
        }));

        // LinearEffectMovement
        list.add(new ClassEntry(LinearEffectMovement.class, () -> {
            FloatInput speedInput = new FloatInput();
            FloatInput initialDistanceInput = new FloatInput();
            BooleanCheckBox destroyAtTargetPositionCheckBox = new BooleanCheckBox();
            BooleanCheckBox relativeCheckBox = new BooleanCheckBox();
            bind(speedInput, "speed", LinearEffectMovement.class);
            bind(destroyAtTargetPositionCheckBox, "destroyAtTargetPosition", LinearEffectMovement.class);
            bind(relativeCheckBox, "relative", LinearEffectMovement.class);
            bind(initialDistanceInput, "initialDistance", LinearEffectMovement.class);
            return LayoutUtils.createHorizontalLabelledBox("effectMovementEditor.destroyAtTargetPosition", destroyAtTargetPositionCheckBox, "effectMovementEditor.relative", relativeCheckBox,
                    "effectMovementEditor.initialDistance", initialDistanceInput, "statType.speed", speedInput);
        }));

        // BackToOriginEffectMovement
        list.add(new ClassEntry(BackToOriginEffectMovement.class, () -> {
            FloatInput speedInput = new FloatInput();
            bind(speedInput, "speed", BackToOriginEffectMovement.class);
            return LayoutUtils.labelled("statType.speed", speedInput);
        }));

        return list;
    }

    @Override
    protected void initialize(EffectMovement oldInstance, EffectMovement newInstance) {
        // Nothing
    }

}
