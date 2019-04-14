package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;

public class AlterationEditor extends InstanceChangingElementEditor<Alteration> {

	private static final long serialVersionUID = 1L;

	public AlterationEditor() {
		super("alterationType");
		LayoutUtils.addHorizontally(this, 1, LayoutUtils.labelled("generic.type", getTypeChooser()), getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		// InstantDamageAlteration
		FloatInput damageAmountInput = new FloatInput(Bounds.positive());
		bind(damageAmountInput, InstantDamageAlteration::getAmount, InstantDamageAlteration::setAmount, InstantDamageAlteration.class);
		entries.add(new ClassEntry(InstantDamageAlteration.class, LayoutUtils.labelled("alterationEditor.amount", damageAmountInput)));

		// InstantHealAlteration
		FloatInput healAmountInput = new FloatInput(Bounds.positive());
		bind(healAmountInput, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
		entries.add(new ClassEntry(InstantHealAlteration.class, LayoutUtils.labelled("alterationEditor.amount", healAmountInput)));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		// No common field
	}

}
