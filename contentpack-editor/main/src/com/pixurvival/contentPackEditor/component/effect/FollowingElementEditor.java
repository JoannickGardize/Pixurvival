package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.abilitySet.OffsetAngleEffectEditor;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.FollowingCreature;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;

public class FollowingElementEditor extends InstanceChangingElementEditor<FollowingElement> {

	private static final long serialVersionUID = 1L;

	public FollowingElementEditor() {
		super("followingElementType");

		TimeInput delayInput = new TimeInput();

		bind(delayInput, FollowingElement::getDelay, FollowingElement::setDelay);

		setLayout(new BorderLayout());
		add(LayoutUtils.createHorizontalLabelledBox("generic.delay", delayInput, "generic.type", getTypeChooser()), BorderLayout.NORTH);
		add(getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		// FollowingEffect
		OffsetAngleEffectEditor offsetAngleEffectEditor = new OffsetAngleEffectEditor();
		bind(offsetAngleEffectEditor, FollowingEffect::getOffsetAngleEffect, FollowingEffect::setOffsetAngleEffect, FollowingEffect.class);
		entries.add(new ClassEntry(FollowingEffect.class, offsetAngleEffectEditor));

		// FollowingCreature
		ElementChooserButton<Creature> creatureChooser = new ElementChooserButton<>(Creature.class);
		BooleanCheckBox ownedCheckBox = new BooleanCheckBox();
		StatAmountEditor strengthEditor = new StatAmountEditor();
		StatAmountEditor agilityEditor = new StatAmountEditor();
		StatAmountEditor intelligenceEditor = new StatAmountEditor();
		bind(creatureChooser, FollowingCreature::getCreature, FollowingCreature::setCreature, FollowingCreature.class);
		bind(ownedCheckBox, FollowingCreature::isOwned, FollowingCreature::setOwned, FollowingCreature.class);
		bind(strengthEditor, FollowingCreature::getStrengthBonus, FollowingCreature::setStrengthBonus, FollowingCreature.class);
		bind(agilityEditor, FollowingCreature::getAgilityBonus, FollowingCreature::setAgilityBonus, FollowingCreature.class);
		bind(intelligenceEditor, FollowingCreature::getIntelligenceBonus, FollowingCreature::setIntelligenceBonus, FollowingCreature.class);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(LayoutUtils.createHorizontalLabelledBox("elementType.creature", creatureChooser, "followingCreatureEditor.owned", ownedCheckBox), BorderLayout.NORTH);
		JPanel statPanel = LayoutUtils.createVerticalLabelledBox("statType.strength", strengthEditor, "statType.agility", agilityEditor, "statType.intelligence", intelligenceEditor);
		statPanel.setBorder(LayoutUtils.createGroupBorder("followingCreatureEditor.bonusStats"));
		panel.add(statPanel, BorderLayout.CENTER);
		entries.add(new ClassEntry(FollowingCreature.class, panel));

		return entries;
	}

	@Override
	protected void initialize(FollowingElement oldInstance, FollowingElement newInstance) {
		newInstance.setDelay(oldInstance.getDelay());
	}

}
