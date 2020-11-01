package com.pixurvival.contentPackEditor.component.gameMode;

import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.contentPackEditor.component.valueComponent.StringInput;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.contentPack.gameMode.role.Role.Visibility;

public class RoleEditor extends ElementEditor<Role> {

	private static final String VISIBILITY_TRANSLATION_PREFFIX = "roleEditor.visibility";

	private static final long serialVersionUID = 1L;

	public RoleEditor(Supplier<Collection<Role>> roleCollectionSupplier) {
		StringInput nameInput = new StringInput(1);
		EnumChooser<Visibility> teammatesVisibilityChoooser = new EnumChooser<>(Visibility.class, VISIBILITY_TRANSLATION_PREFFIX);
		EnumChooser<Visibility> enemiesVisibilityChoooser = new EnumChooser<>(Visibility.class, VISIBILITY_TRANSLATION_PREFFIX);
		IntegerInput minimumPerTeamInput = new IntegerInput(Bounds.positive());
		IntegerInput maximumPerTeamInput = new IntegerInput(Bounds.positive());
		PercentInput recommandedRatioPerTeamInput = new PercentInput(Bounds.positive());
		StarterKitEditor starterKitEditor = new StarterKitEditor();
		starterKitEditor.setBorder(LayoutUtils.createGroupBorder("roleEditor.starterKit"));
		WinConditionEditor winConditionEditor = new WinConditionEditor(roleCollectionSupplier);
		winConditionEditor.setBorder(LayoutUtils.createGroupBorder("roleEditor.winCondition"));

		bind(nameInput, Role::getName, Role::setName);
		bind(teammatesVisibilityChoooser, Role::getTeammatesVisiblity, Role::setTeammatesVisiblity);
		bind(enemiesVisibilityChoooser, Role::getEnemiesVisiblity, Role::setEnemiesVisiblity);
		bind(minimumPerTeamInput, Role::getMinimumPerTeam, Role::setMinimumPerTeam);
		bind(maximumPerTeamInput, Role::getMaximumPerTeam, Role::setMaximumPerTeam);
		bind(recommandedRatioPerTeamInput, Role::getRecommandedRatioPerTeam, Role::setRecommandedRatioPerTeam);
		bind(starterKitEditor, Role::getStarterKit, Role::setStarterKit);
		bind(winConditionEditor, Role::getWinCondition, Role::setWinCondition);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(LayoutUtils.single(LayoutUtils.labelled("generic.name", nameInput)));
		JPanel visibilityPanel = LayoutUtils.createHorizontalLabelledBox("roleEditor.teammatesVisibility", teammatesVisibilityChoooser, "roleEditor.enemiesVisibility", enemiesVisibilityChoooser);
		visibilityPanel.setBorder(LayoutUtils.createGroupBorder(VISIBILITY_TRANSLATION_PREFFIX));
		add(visibilityPanel);
		JPanel divisionPanel = LayoutUtils.createHorizontalLabelledBox("generic.minimum", minimumPerTeamInput, "generic.maximum", maximumPerTeamInput, "roleEditor.recommandedRatio",
				recommandedRatioPerTeamInput);
		divisionPanel.setBorder(LayoutUtils.createGroupBorder("roleEditor.division"));
		add(divisionPanel);
		add(starterKitEditor);
		add(winConditionEditor);

	}
}
