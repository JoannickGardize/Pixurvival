package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
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
		StatAmountEditor damageAmountEditor = new StatAmountEditor();
		bind(damageAmountEditor, InstantDamageAlteration::getAmount, InstantDamageAlteration::setAmount, InstantDamageAlteration.class);
		entries.add(new ClassEntry(InstantDamageAlteration.class, LayoutUtils.labelled("alterationEditor.amount", damageAmountEditor)));

		// InstantHealAlteration
		StatAmountEditor healAmountEditor = new StatAmountEditor();
		bind(healAmountEditor, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
		entries.add(new ClassEntry(InstantHealAlteration.class, LayoutUtils.labelled("alterationEditor.amount", healAmountEditor)));

		// InstantEatAlteration
		StatAmountEditor eatAmountEditor = new StatAmountEditor();
		bind(eatAmountEditor, InstantEatAlteration::getAmount, InstantEatAlteration::setAmount, InstantEatAlteration.class);
		entries.add(new ClassEntry(InstantEatAlteration.class, LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor)));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		// No common field
	}

}
