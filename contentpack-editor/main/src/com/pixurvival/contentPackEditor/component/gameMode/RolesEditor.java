package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.util.Collections;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NullableElementHelper;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.contentPack.gameMode.role.Roles;
import com.pixurvival.core.contentPack.gameMode.role.Roles.SelectionMode;

public class RolesEditor extends ElementEditor<Roles> {

	private static final long serialVersionUID = 1L;

	private NullableElementHelper<Roles> nullableElementHelper = new NullableElementHelper<>(this);

	public RolesEditor() {
		EnumChooser<SelectionMode> selectionModeChooser = new EnumChooser<>(SelectionMode.class, "rolesEditor.selectionMode");
		ListEditor<Role> roleListEditor = new VerticalListEditor<>(() -> new RoleEditor(() -> getValue() == null ? Collections.emptyList() : getValue().getRoles()), Role::new);
		roleListEditor.setOneRequired(true);

		bind(selectionModeChooser, Roles::getSelectionMode, Roles::setSelectionMode);
		bind(roleListEditor, Roles::getRoles, Roles::setRoles);

		JPanel contentPanel = nullableElementHelper.getNotNullPanel();
		contentPanel.setLayout(new BorderLayout(5, 5));
		contentPanel.add(LayoutUtils.single(LayoutUtils.labelled("rolesEditor.selectionMode", selectionModeChooser)), BorderLayout.NORTH);
		contentPanel.add(roleListEditor);

		nullableElementHelper.build(Roles::new);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		nullableElementHelper.onValueChanged();
	}

	@Override
	public boolean isNullable() {
		return true;
	}
}
