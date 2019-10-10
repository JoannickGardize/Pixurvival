package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.item.StatModifierEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.AngleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.alteration.AddItemAlteration;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.ContinuousDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.FixedMovementAlteration;
import com.pixurvival.core.livingEntity.alteration.FixedMovementAlteration.FixedMovementOrigin;
import com.pixurvival.core.livingEntity.alteration.FollowingElementAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;
import com.pixurvival.core.livingEntity.alteration.InstantHealAlteration;
import com.pixurvival.core.livingEntity.alteration.OverridingSpriteSheetAlteration;
import com.pixurvival.core.livingEntity.alteration.PersistentAlteration;
import com.pixurvival.core.livingEntity.alteration.StatAlteration;

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

		// ContinuousDamageAlteration
		damageAmountEditor = new StatAmountEditor();
		bind(damageAmountEditor, ContinuousDamageAlteration::getDamagePerSecond, ContinuousDamageAlteration::setDamagePerSecond, ContinuousDamageAlteration.class);
		entries.add(new ClassEntry(ContinuousDamageAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amountPerSecond", damageAmountEditor))));

		// InstantHealAlteration
		StatAmountEditor healAmountEditor = new StatAmountEditor();
		bind(healAmountEditor, InstantHealAlteration::getAmount, InstantHealAlteration::setAmount, InstantHealAlteration.class);
		entries.add(new ClassEntry(InstantHealAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", healAmountEditor))));

		// InstantEatAlteration
		StatAmountEditor eatAmountEditor = new StatAmountEditor();
		bind(eatAmountEditor, InstantEatAlteration::getAmount, InstantEatAlteration::setAmount, InstantEatAlteration.class);
		entries.add(new ClassEntry(InstantEatAlteration.class, LayoutUtils.single(LayoutUtils.labelled("alterationEditor.amount", eatAmountEditor))));

		// StatAlteration
		TimeInput durationInput = new TimeInput();
		EnumChooser<PersistentAlteration.StackPolicy> stackPolicyChooser = new EnumChooser<>(PersistentAlteration.StackPolicy.class);
		StatModifierEditor statModifierEditor = new StatModifierEditor();
		bind(durationInput, StatAlteration::getDuration, StatAlteration::setDuration, StatAlteration.class);
		bind(stackPolicyChooser, StatAlteration::getStackPolicy, StatAlteration::setStackPolicy, StatAlteration.class);
		bind(statModifierEditor, StatAlteration::getStatModifier, StatAlteration::setStatModifier, StatAlteration.class);
		JPanel statAlterationPanel = new JPanel(new BorderLayout());
		statAlterationPanel.add(LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "alterationEditor.stackPolicy", stackPolicyChooser), BorderLayout.WEST);
		statAlterationPanel.add(statModifierEditor, BorderLayout.CENTER);
		entries.add(new ClassEntry(StatAlteration.class, statAlterationPanel));

		// FixedMovementAlteration
		durationInput = new TimeInput();
		EnumChooser<FixedMovementOrigin> originChooser = new EnumChooser<>(FixedMovementOrigin.class);
		AngleInput relativeAngleInput = new AngleInput();
		AngleInput randomAngleInput = new AngleInput();
		StatAmountEditor speedEditor = new StatAmountEditor();
		bind(durationInput, FixedMovementAlteration::getDuration, FixedMovementAlteration::setDuration, FixedMovementAlteration.class);
		bind(originChooser, FixedMovementAlteration::getOrigin, FixedMovementAlteration::setOrigin, FixedMovementAlteration.class);
		bind(relativeAngleInput, FixedMovementAlteration::getRelativeAngle, FixedMovementAlteration::setRelativeAngle, FixedMovementAlteration.class);
		bind(randomAngleInput, FixedMovementAlteration::getRandomAngle, FixedMovementAlteration::setRandomAngle, FixedMovementAlteration.class);
		bind(speedEditor, FixedMovementAlteration::getSpeed, FixedMovementAlteration::setSpeed, FixedMovementAlteration.class);
		JPanel fmaPanel = new JPanel();
		LayoutUtils.addHorizontally(fmaPanel, LayoutUtils.createVerticalLabelledBox("generic.duration", durationInput, "alterationEditor.origin", originChooser),
				LayoutUtils.createVerticalLabelledBox("offsetAngleEffect.offsetAngle", relativeAngleInput, "offsetAngleEffect.randomAngle", randomAngleInput),
				LayoutUtils.createVerticalLabelledBox("statType.speed", speedEditor));
		entries.add(new ClassEntry(FixedMovementAlteration.class, fmaPanel));

		// OverridingSpriteSheetEditor

		durationInput = new TimeInput();
		ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, LayoutUtils.getSpriteSheetIconProvider(), false);
		bind(durationInput, OverridingSpriteSheetAlteration::getDuration, OverridingSpriteSheetAlteration::setDuration, OverridingSpriteSheetAlteration.class);
		bind(spriteSheetChooser, OverridingSpriteSheetAlteration::getSpriteSheet, OverridingSpriteSheetAlteration::setSpriteSheet, OverridingSpriteSheetAlteration.class);
		entries.add(new ClassEntry(OverridingSpriteSheetAlteration.class, LayoutUtils.createHorizontalLabelledBox("generic.duration", durationInput, "elementType.spriteSheet", spriteSheetChooser)));

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
