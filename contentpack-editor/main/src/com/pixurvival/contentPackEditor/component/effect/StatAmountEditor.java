package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.core.livingEntity.alteration.StatAmount;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;

public class StatAmountEditor extends ElementEditor<StatAmount> {

	private static final long serialVersionUID = 1L;

	public StatAmountEditor() {

		// Construction

		FloatInput baseInput = new FloatInput(Bounds.positive());
		ListEditor<StatMultiplier> statMultipliersEditor = new HorizontalListEditor<>(() -> {
			StatMultiplierEditor editor = new StatMultiplierEditor();
			editor.setBorder(LayoutUtils.createBorder());
			return editor;
		}, StatMultiplier::new);

		// Binding

		bind(baseInput, StatAmount::getBase, StatAmount::setBase);
		bind(statMultipliersEditor, StatAmount::getStatMultipliers, StatAmount::setStatMultipliers);

		// Layouting

		LayoutUtils.addHorizontally(this, LayoutUtils.labelled("generic.base", baseInput), statMultipliersEditor);
	}

}
