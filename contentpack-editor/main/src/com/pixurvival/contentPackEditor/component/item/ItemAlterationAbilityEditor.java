package com.pixurvival.contentPackEditor.component.item;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.abilitySet.AlterationAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NullableElementHelper;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;

public class ItemAlterationAbilityEditor extends AlterationAbilityEditor<ItemAlterationAbility> {

	private static final long serialVersionUID = 1L;

	private NullableElementHelper<ItemStack> nullableElementHelper;

	public ItemAlterationAbilityEditor(boolean useScrollPane) {
		super(ItemAlterationAbility.class);
		ItemStackEditor ammunitionEditor = new ItemStackEditor();
		nullableElementHelper = new NullableElementHelper<>(ammunitionEditor);
		nullableElementHelper.getNotNullPanel().setLayout(new BorderLayout());
		nullableElementHelper.getNotNullPanel().add(ammunitionEditor, BorderLayout.CENTER);
		JPanel ammunitionPanel = new JPanel();
		ammunitionPanel.setBorder(LayoutUtils.createBorder());

		bind(ammunitionEditor, "ammunition");

		build(useScrollPane, "alterationAbilityEditor.ammunition", ammunitionPanel);
		nullableElementHelper.build(ItemStack::new, ammunitionPanel);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		nullableElementHelper.onValueChanged();
	}
}
