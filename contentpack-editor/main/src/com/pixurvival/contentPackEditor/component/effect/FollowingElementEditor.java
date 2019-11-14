package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.abilitySet.OffsetAngleEffectEditor;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.FollowingCreature;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;

public class FollowingElementEditor extends InstanceChangingElementEditor<FollowingElement> {

	private static final long serialVersionUID = 1L;

	public FollowingElementEditor(Object... topLineLabelAndComponents) {
		super("followingElementType", null);

		setLayout(new BorderLayout());
		int length = topLineLabelAndComponents.length;
		Object[] fullLabelAndComponents = Arrays.copyOf(topLineLabelAndComponents, topLineLabelAndComponents.length + 2);
		fullLabelAndComponents[length] = "generic.type";
		fullLabelAndComponents[length + 1] = getTypeChooser();
		add(LayoutUtils.createHorizontalLabelledBox(fullLabelAndComponents), BorderLayout.NORTH);
		add(getSpecificPartPanel(), BorderLayout.CENTER);
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		// FollowingEffect
		OffsetAngleEffectEditor offsetAngleEffectEditor = new OffsetAngleEffectEditor();
		bind(offsetAngleEffectEditor, FollowingEffect::getOffsetAngleEffect, FollowingEffect::setOffsetAngleEffect, FollowingEffect.class);
		entries.add(new ClassEntry(FollowingEffect.class, offsetAngleEffectEditor));

		// FollowingCreature
		ElementChooserButton<Creature> creatureChooser = new ElementChooserButton<>(Creature.class);
		BooleanCheckBox ownedCheckBox = new BooleanCheckBox();
		StatFormulaEditor strengthEditor = new StatFormulaEditor();
		StatFormulaEditor agilityEditor = new StatFormulaEditor();
		StatFormulaEditor intelligenceEditor = new StatFormulaEditor();
		bind(creatureChooser, FollowingCreature::getCreature, FollowingCreature::setCreature, FollowingCreature.class);
		bind(ownedCheckBox, FollowingCreature::isOwned, FollowingCreature::setOwned, FollowingCreature.class);
		bind(strengthEditor, FollowingCreature::getStrengthBonus, FollowingCreature::setStrengthBonus, FollowingCreature.class);
		bind(agilityEditor, FollowingCreature::getAgilityBonus, FollowingCreature::setAgilityBonus, FollowingCreature.class);
		bind(intelligenceEditor, FollowingCreature::getIntelligenceBonus, FollowingCreature::setIntelligenceBonus, FollowingCreature.class);
		JPanel statPanel = LayoutUtils.createVerticalLabelledBox("statType.strength", strengthEditor, "statType.agility", agilityEditor, "statType.intelligence", intelligenceEditor);
		statPanel.setBorder(LayoutUtils.createGroupBorder("followingCreatureEditor.bonusStats"));
		entries.add(new ClassEntry(FollowingCreature.class,
				LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("elementType.creature", creatureChooser, "followingCreatureEditor.owned", ownedCheckBox), statPanel)));

		return entries;
	}

	@Override
	protected void initialize(FollowingElement oldInstance, FollowingElement newInstance) {
	}

}
