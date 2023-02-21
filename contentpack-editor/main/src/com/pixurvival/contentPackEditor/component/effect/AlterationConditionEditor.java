package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.core.alteration.condition.AlterationCondition;
import com.pixurvival.core.alteration.condition.HealthAlterationCondition;
import com.pixurvival.core.util.FloatComparison;

public class AlterationConditionEditor extends InstanceChangingElementEditor<AlterationCondition> {

	public AlterationConditionEditor() {
		super(AlterationCondition.class, "alterationConditionType");
		LayoutUtils.fill(this, getSpecificPartPanel());
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();
		entries.add(new ClassEntry(HealthAlterationCondition.class, () -> {
			EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
			PercentInput percentValueInput = new PercentInput();
			bind(operatorChooser, "operator", HealthAlterationCondition.class);
			bind(percentValueInput, "percentValue", HealthAlterationCondition.class);
			return LayoutUtils.createHorizontalBox(LayoutUtils.label("generic.health"), operatorChooser, percentValueInput);
		}));
		return entries;
	}

	@Override
	protected void initialize(AlterationCondition oldInstance, AlterationCondition newInstance) {
	}

}
