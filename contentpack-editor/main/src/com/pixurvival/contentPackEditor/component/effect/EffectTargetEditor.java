package com.pixurvival.contentPackEditor.component.effect;

import java.awt.BorderLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.AlterationTarget;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;

public class EffectTargetEditor extends ElementEditor<EffectTarget> {

	private static final long serialVersionUID = 1L;

	public EffectTargetEditor() {

		// Construction
		EnumChooser<TargetType> targetTypeChooser = new EnumChooser<>(TargetType.class);
		BooleanCheckBox destroyCheckBox = new BooleanCheckBox();
		ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(() -> new AlterationEditor(AlterationTarget.values()), InstantDamageAlteration::new, ListEditor.HORIZONTAL, false);

		// Binding
		bind(targetTypeChooser, EffectTarget::getTargetType, EffectTarget::setTargetType);
		bind(destroyCheckBox, EffectTarget::isDestroyWhenCollide, EffectTarget::setDestroyWhenCollide);
		bind(alterationsEditor, EffectTarget::getAlterations, EffectTarget::setAlterations);

		// Layouting
		alterationsEditor.setBorder(LayoutUtils.createGroupBorder("effectTargetEditor.alterations"));
		setLayout(new BorderLayout());
		add(LayoutUtils.createHorizontalLabelledBox("generic.target", targetTypeChooser, "effectTargetEditor.destroyWhenCollide", destroyCheckBox), BorderLayout.NORTH);
		add(alterationsEditor, BorderLayout.SOUTH);
	}

}
