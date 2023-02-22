package com.pixurvival.contentPackEditor.component.gameMode;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.IntOperator;
import com.pixurvival.core.contentPack.gameMode.TeamSet;
import com.pixurvival.core.contentPack.gameMode.role.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class WinConditionEditor extends InstanceChangingElementEditor<WinCondition> {

    private static final long serialVersionUID = 1L;

    public WinConditionEditor(Supplier<Collection<Role>> roleCollectionSupplier) {
        super(WinCondition.class, "roleEditor.winConditionType", roleCollectionSupplier);
        setLayout(new BorderLayout(5, 5));
        add(getTypeChooser(), BorderLayout.NORTH);
        add(getSpecificPartPanel(), BorderLayout.CENTER);
    }

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();
        entries.add(new ClassEntry(TeamSurvivedWinCondition.class, JPanel::new));
        entries.add(new ClassEntry(SurviveWinCondition.class, JPanel::new));
        @SuppressWarnings("unchecked")
        Supplier<Collection<Role>> roleCollectionSupplier = (Supplier<Collection<Role>>) params;
        entries.add(new ClassEntry(RemainingRolesWinCondition.class, () -> {
            ListEditor<Role> rolesList = new HorizontalListEditor<>(() -> new ElementChooserButton<>(roleCollectionSupplier), () -> null);
            EnumChooser<TeamSet> teamTakenIntoAccountChooser = new EnumChooser<>(TeamSet.class);
            EnumChooser<IntOperator> operatorChooser = new EnumChooser<>(IntOperator.class);
            IntegerInput valueInput = new IntegerInput();

            bind(rolesList, "roles", RemainingRolesWinCondition.class);
            bind(teamTakenIntoAccountChooser, "teamSet", RemainingRolesWinCondition.class);
            bind(operatorChooser, "operator", RemainingRolesWinCondition.class);
            bind(valueInput, "value", RemainingRolesWinCondition.class);

            JPanel remainingRolesPanel = new JPanel();
            rolesList.setBorder(LayoutUtils.createGroupBorder("winConditionEditor.roleSum"));
            remainingRolesPanel.setLayout(new BoxLayout(remainingRolesPanel, BoxLayout.Y_AXIS));
            remainingRolesPanel.add(rolesList);
            remainingRolesPanel.add(LayoutUtils.createHorizontalBox(LayoutUtils.labelled("winConditionEditor.fromTeam", teamTakenIntoAccountChooser), operatorChooser, valueInput));
            return remainingRolesPanel;
        }));

        return entries;
    }

    @Override
    protected void initialize(WinCondition oldInstance, WinCondition newInstance) {
        // Nothing
    }
}
