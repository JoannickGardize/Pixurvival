package com.pixurvival.contentPackEditor.component.gameMode;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.NoEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamCondition;

public class EndGameConditionEditor extends InstanceChangingElementEditor<EndGameCondition> {

	public EndGameConditionEditor() {
		super("endGameConditionType", null);

		LayoutUtils.addHorizontally(this, 1, getTypeChooser(), getSpecificPartPanel());
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		entries.add(new ClassEntry(NoEndCondition.class, new JPanel()));

		IntegerInput remainingTeamCondition = new IntegerInput(Bounds.positive());
		bind(remainingTeamCondition, RemainingTeamCondition::getRemainingTeamCondition, RemainingTeamCondition::setRemainingTeamCondition, RemainingTeamCondition.class);
		entries.add(new ClassEntry(RemainingTeamCondition.class, LayoutUtils.single(LayoutUtils.labelled("remainingTeamConditionEditor.remainingTeamCondition", remainingTeamCondition))));
		return entries;
	}

	@Override
	protected void initialize(EndGameCondition oldInstance, EndGameCondition newInstance) {
	}

}
