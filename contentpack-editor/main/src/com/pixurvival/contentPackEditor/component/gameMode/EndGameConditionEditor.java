package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingRolesEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.RemainingTeamEndCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.TimeEndCondition;
import com.pixurvival.core.contentPack.gameMode.role.Role;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class EndGameConditionEditor extends InstanceChangingElementEditor<EndGameCondition> {

    public EndGameConditionEditor(Supplier<Collection<Role>> roleCollectionSupplier) {
        super(EndGameCondition.class, "endGameConditionType", roleCollectionSupplier);
        LayoutUtils.addHorizontally(this, 1, getTypeChooser(), getSpecificPartPanel());
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();

        entries.add(new ClassEntry(RemainingTeamEndCondition.class, () -> {
            IntegerInput remainingTeamCondition = new IntegerInput();
            bind(remainingTeamCondition, "remainingTeamCondition", RemainingTeamEndCondition.class);
            return LayoutUtils.single(LayoutUtils.labelled("endGameConditionEditor.remainingTeamCondition", remainingTeamCondition));
        }));
        @SuppressWarnings("unchecked")
        Supplier<Collection<Role>> roleCollectionSupplier = (Supplier<Collection<Role>>) params;
        entries.add(new ClassEntry(RemainingRolesEndCondition.class, () -> {
            ListEditor<Role> rolesEditor = new HorizontalListEditor<>(() -> new ElementChooserButton<>(roleCollectionSupplier), () -> null);
            rolesEditor.setBorder(LayoutUtils.createGroupBorder("winConditionEditor.roleSum"));
            BooleanCheckBox countPerTeamCheckbox = new BooleanCheckBox();
            IntegerInput valueInput = new IntegerInput();

            bind(rolesEditor, "roles", RemainingRolesEndCondition.class);
            bind(countPerTeamCheckbox, "countPerTeam", RemainingRolesEndCondition.class);
            bind(valueInput, "value", RemainingRolesEndCondition.class);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(rolesEditor);
            panel.add(LayoutUtils.createHorizontalLabelledBox("endGameConditionEditor.countPerTeam", countPerTeamCheckbox, "endGameConditionEditor.lessOrEqualTo", valueInput));
            return panel;
        }));
        entries.add(new ClassEntry(TimeEndCondition.class, () -> {
            TimeInput timeInput = new TimeInput();
            bind(timeInput, "time", TimeEndCondition.class);
            return LayoutUtils.single(LayoutUtils.labelled("generic.time", timeInput));
        }));

        return entries;
    }

    @Override
    protected void initialize(EndGameCondition oldInstance, EndGameCondition newInstance) {
    }

}
