package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.contentPack.effect.AnchorEffectMovement;
import com.pixurvival.core.contentPack.effect.EffectMovement;
import com.pixurvival.core.contentPack.effect.LinearEffectMovement;
import com.pixurvival.core.contentPack.effect.StaticEffectMovement;

public class EffectMovementEditor extends InstanceChangingElementEditor<EffectMovement> {

	private static final long serialVersionUID = 1L;

	public EffectMovementEditor() {
		super("effectMovementType");
		LayoutUtils.addHorizontally(this, 1, LayoutUtils.labelled("generic.type", getTypeChooser()), getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> list = new ArrayList<>();

		// StaticEffectMovement
		DoubleInput minDistanceInput = new DoubleInput(Bounds.positive());
		DoubleInput maxDistanceInput = new DoubleInput(Bounds.positive());
		bind(minDistanceInput, StaticEffectMovement::getMinDistance, StaticEffectMovement::setMinDistance, StaticEffectMovement.class);
		bind(maxDistanceInput, StaticEffectMovement::getMaxDistance, StaticEffectMovement::setMaxDistance, StaticEffectMovement.class);
		list.add(new ClassEntry(StaticEffectMovement.class,
				LayoutUtils.createHorizontalBox(LayoutUtils.labelled("generic.minDistance", minDistanceInput), LayoutUtils.labelled("generic.maxDistance", maxDistanceInput))));

		// AnchorEffectMovement
		DoubleInput distanceInput = new DoubleInput(Bounds.positive());
		bind(distanceInput, AnchorEffectMovement::getDistance, AnchorEffectMovement::setDistance, AnchorEffectMovement.class);
		list.add(new ClassEntry(AnchorEffectMovement.class, LayoutUtils.labelled("generic.distance", distanceInput)));

		// LinearEffectMovement
		DoubleInput speedInput = new DoubleInput(Bounds.positive());
		bind(speedInput, LinearEffectMovement::getSpeed, LinearEffectMovement::setSpeed, LinearEffectMovement.class);
		list.add(new ClassEntry(LinearEffectMovement.class, LayoutUtils.labelled("statType.speed", speedInput)));

		return list;
	}

	@Override
	protected void initialize(EffectMovement oldInstance, EffectMovement newInstance) {
		// Nothing
	}

}
