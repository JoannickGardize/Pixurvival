package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DistanceCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.DoNothingBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayFromLightBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.VanishBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderAnchor;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderBehavior;

public class BehaviorEditor extends InstanceChangingElementEditor<Behavior> {

	private static final long serialVersionUID = 1L;

	private ListEditor<ChangeCondition> changeConditionsEditor;

	public BehaviorEditor(Supplier<Collection<Behavior>> behaviorCollectionSupplier) {
		super("behaviorType", null);

		changeConditionsEditor = new VerticalListEditor<>(() -> {
			ChangeConditionEditor editor = new ChangeConditionEditor(behaviorCollectionSupplier);
			editor.setBorder(LayoutUtils.createBorder());
			return editor;
		}, DistanceCondition::new, ListEditor.HORIZONTAL, false);

		// Construction

		StringInput nameInput = new StringInput(1);
		IntegerInput abilityIndexInput = new IntegerInput(Bounds.min(-1));

		// Binding

		bind(nameInput, Behavior::getName, Behavior::setName);
		bind(abilityIndexInput, Behavior::getAbilityToUseId, Behavior::setAbilityToUseId);
		bind(changeConditionsEditor, Behavior::getChangeConditions, Behavior::setChangeConditions);

		// Layouting

		Component nameComp = LayoutUtils.labelled("generic.name", nameInput);
		Component abilityIndexComp = LayoutUtils.labelled("behaviorEditor.abilityIndex", abilityIndexInput);
		Component typeChooserComp = LayoutUtils.labelled("generic.type", getTypeChooser());
		JPanel topPanel = LayoutUtils.createHorizontalBox(nameComp, abilityIndexComp, typeChooserComp);
		changeConditionsEditor.setBorder(LayoutUtils.createGroupBorder("behaviorEditor.changeConditions"));
		LayoutUtils.addVertically(this, topPanel, getSpecificPartPanel(), changeConditionsEditor);

	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> classEntries = new ArrayList<>();

		// GET_AWAY
		EnumChooser<BehaviorTarget> targetChooser = new EnumChooser<>(BehaviorTarget.class);
		bind(targetChooser, GetAwayBehavior::getTargetType, GetAwayBehavior::setTargetType, GetAwayBehavior.class);
		classEntries.add(new ClassEntry(GetAwayBehavior.class, LayoutUtils.createHorizontalBox(targetChooser)));

		// MOVE_TOWARD
		targetChooser = new EnumChooser<>(BehaviorTarget.class);
		DoubleInput minDistanceInput = new DoubleInput(Bounds.positive());
		AngleInput randomAngleInput = new AngleInput();
		bind(minDistanceInput, MoveTowardBehavior::getMinDistance, MoveTowardBehavior::setMinDistance, MoveTowardBehavior.class);
		bind(targetChooser, MoveTowardBehavior::getTargetType, MoveTowardBehavior::setTargetType, MoveTowardBehavior.class);
		bind(randomAngleInput, MoveTowardBehavior::getRandomAngle, MoveTowardBehavior::setRandomAngle, MoveTowardBehavior.class);
		classEntries.add(new ClassEntry(MoveTowardBehavior.class,
				LayoutUtils.createHorizontalLabelledBox("generic.target", targetChooser, "generic.minDistance", minDistanceInput, "generic.randomAngle", randomAngleInput)));

		// TURN_AROUND
		targetChooser = new EnumChooser<>(BehaviorTarget.class);
		minDistanceInput = new DoubleInput(Bounds.positive());
		DoubleInput maxDistanceInput = new DoubleInput(Bounds.positive());
		bind(minDistanceInput, TurnAroundBehavior::getMinDistance, TurnAroundBehavior::setMinDistance, TurnAroundBehavior.class);
		bind(maxDistanceInput, TurnAroundBehavior::getMaxDistance, TurnAroundBehavior::setMaxDistance, TurnAroundBehavior.class);
		bind(targetChooser, TurnAroundBehavior::getTargetType, TurnAroundBehavior::setTargetType, TurnAroundBehavior.class);
		classEntries.add(new ClassEntry(TurnAroundBehavior.class,
				LayoutUtils.createHorizontalLabelledBox("generic.target", targetChooser, "generic.minDistance", minDistanceInput, "generic.maxDistance", maxDistanceInput)));

		// WANDER
		DoubleInput moveRateInput = new DoubleInput(Bounds.positive());
		DoubleInput forwardFactorInput = new DoubleInput(Bounds.positive());
		EnumChooser<WanderAnchor> wanderAnchorChooser = new EnumChooser<>(WanderAnchor.class);
		bind(wanderAnchorChooser, WanderBehavior::getAnchorType, WanderBehavior::setAnchorType, WanderBehavior.class);
		bind(moveRateInput, WanderBehavior::getMoveRate, WanderBehavior::setMoveRate, WanderBehavior.class);
		bind(forwardFactorInput, WanderBehavior::getForwardFactor, WanderBehavior::setForwardFactor, WanderBehavior.class);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.anchor", wanderAnchorChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.moveRate", moveRateInput, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(panel, "wanderBehavior.forwardFactor", forwardFactorInput, gbc);
		classEntries.add(new ClassEntry(WanderBehavior.class, panel));

		// GET_AWAY_FROM_LIGHT
		classEntries.add(new ClassEntry(GetAwayFromLightBehavior.class, new JPanel()));

		// VANISH
		classEntries.add(new ClassEntry(VanishBehavior.class, new JPanel()));

		// Do nothing
		classEntries.add(new ClassEntry(DoNothingBehavior.class, new JPanel()));

		return classEntries;
	}

	@Override
	protected void initialize(Behavior oldInstance, Behavior newInstance) {
		newInstance.setName(oldInstance.getName());
		newInstance.setAbilityToUseId(oldInstance.getAbilityToUseId());
		newInstance.setChangeConditions(oldInstance.getChangeConditions());
	}
}
