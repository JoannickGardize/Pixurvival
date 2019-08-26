package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
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
		entries.add(new ClassEntry(InstantDamageAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", damageAmountEditor))));

		// InstantHealAlteration
		StatAmountEditor healAmountEditor = new StatAmountEditor();
		bind(healAmountEditor, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
		entries.add(new ClassEntry(InstantHealAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", healAmountEditor))));

		// InstantEatAlteration
		StatAmountEditor eatAmountEditor = new StatAmountEditor();
		bind(eatAmountEditor, InstantEatAlteration::getAmount, InstantEatAlteration::setAmount, InstantEatAlteration.class);
		entries.add(new ClassEntry(InstantEatAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor))));

		// AddItemAlteration
		ItemStackEditor itemStackEditor = new ItemStackEditor();
		BooleanCheckBox dropRemainderCheckbox = new BooleanCheckBox();
		bind(itemStackEditor, AddItemAlteration::getItemStack, AddItemAlteration::setItemStack, AddItemAlteration.class);
		bind(dropRemainderCheckbox, AddItemAlteration::isDropRemainder, AddItemAlteration::setDropRemainder, AddItemAlteration.class);
		entries.add(new ClassEntry(AddItemAlteration.class,
				LayoutUtils.single(LayoutUtils.createHorizontalLabelledBox("elementType.item", itemStackEditor, "alterationEditor.dropRemainder", dropRemainderCheckbox))));

		// FollowingElementAlteration
		FollowingElementEditor followingElementEditor = new FollowingElementEditor();
		bind(followingElementEditor, FollowingElementAlteration::getFollowingElement, FollowingElementAlteration::setFollowingElement, FollowingElementAlteration.class);
		entries.add(new ClassEntry(FollowingElementAlteration.class, LayoutUtils.single(followingElementEditor)));

		return entries;
	}

	@Override
	protected void initialize(Alteration oldInstance, Alteration newInstance) {
		// No common field
	}

}
