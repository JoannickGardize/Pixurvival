package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.contentPack.effect.BackToOriginEffectMovement;
import com.pixurvival.core.contentPack.effect.BoundEffectMovement;
import com.pixurvival.core.contentPack.effect.EffectMovement;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.StaticEffectMovement;

public class EffectMovementEditor extends InstanceChangingElementEditor<EffectMovement> {

	private static final long serialVersionUID = 1L;

	public EffectMovementEditor() {
		super("effectMovementType", null);
		LayoutUtils.addHorizontally(this, 1, LayoutUtils.labelled("generic.type", getTypeChooser()), getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> list = new ArrayList<>();

		// StaticEffectMovement
		FloatInput minDistanceInput = new FloatInput(Bounds.positive());
		FloatInput maxDistanceInput = new FloatInput(Bounds.positive());
		bind(minDistanceInput, StaticEffectMovement::getMinDistance, StaticEffectMovement::setMinDistance, StaticEffectMovement.class);
		bind(maxDistanceInput, StaticEffectMovement::getMaxDistance, StaticEffectMovement::setMaxDistance, StaticEffectMovement.class);
		list.add(new ClassEntry(StaticEffectMovement.class,
				LayoutUtils.createHorizontalBox(LayoutUtils.labelled("generic.minDistance", minDistanceInput), LayoutUtils.labelled("generic.maxDistance", maxDistanceInput))));

		// BoundEffectMovement
		FloatInput distanceInput = new FloatInput(Bounds.positive());
		bind(distanceInput, BoundEffectMovement::getDistance, BoundEffectMovement::setDistance, BoundEffectMovement.class);
		list.add(new ClassEntry(BoundEffectMovement.class, LayoutUtils.labelled("generic.distance", distanceInput)));

		// LinearEffectMovement
		FloatInput speedInput = new FloatInput(Bounds.positive());
		FloatInput initialDistanceInput = new FloatInput(Bounds.positive());
		BooleanCheckBox destroyAtTargetPositionCheckBox = new BooleanCheckBox();
		BooleanCheckBox relativeCheckBox = new BooleanCheckBox();
		bind(speedInput, LinearEffectMovement::getSpeed, LinearEffectMovement::setSpeed, LinearEffectMovement.class);
		bind(destroyAtTargetPositionCheckBox, LinearEffectMovement::isDestroyAtTargetPosition, LinearEffectMovement::setDestroyAtTargetPosition, LinearEffectMovement.class);
		bind(relativeCheckBox, LinearEffectMovement::isRelative, LinearEffectMovement::setRelative, LinearEffectMovement.class);
		bind(initialDistanceInput, LinearEffectMovement::getInitialDistance, LinearEffectMovement::setInitialDistance, LinearEffectMovement.class);
		list.add(new ClassEntry(LinearEffectMovement.class, LayoutUtils.createHorizontalLabelledBox("effectMovementEditor.destroyAtTargetPosition", destroyAtTargetPositionCheckBox,
				"effectMovementEditor.relative", relativeCheckBox, "effectMovementEditor.initialDistance", initialDistanceInput, "statType.speed", speedInput)));

		// BackToOriginEffectMovement
		speedInput = new FloatInput(Bounds.positive());
		bind(speedInput, BackToOriginEffectMovement::getSpeed, BackToOriginEffectMovement::setSpeed, BackToOriginEffectMovement.class);
		list.add(new ClassEntry(BackToOriginEffectMovement.class, LayoutUtils.labelled("statType.speed", speedInput)));

		return list;
	}

	@Override
	protected void initialize(EffectMovement oldInstance, EffectMovement newInstance) {
		// Nothing
	}

}
