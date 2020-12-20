package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.util.DocumentAdapter;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.Version;

// TODO Use ElementEditor ?
public class IdentifierDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	private JTextField nameField = new JTextField(20);
	private JTextField versionField = new JTextField(20);

	public IdentifierDialog() {
		super("identifierDialog.title");

		nameField.getDocument().addDocumentListener(new DocumentAdapter(e -> {
			String name = nameField.getText().trim();
			if (name.length() > 0) {
				FileService.getInstance().getCurrentContentPack().getIdentifier().setName(name);
			}
		}));

		versionField.getDocument().addDocumentListener(new DocumentAdapter(e -> {
			if (versionField.getText().matches("\\d+\\.\\d+")) {
				FileService.getInstance().getCurrentContentPack().getIdentifier().setVersion(new Version(versionField.getText()));
				versionField.setForeground(Color.BLACK);
			} else {
				versionField.setForeground(Color.RED);
			}
		}));

		JPanel content = (JPanel) getContentPane();
		content.setLayout(new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(content, "identifierDialog.name", nameField, gbc);
		LayoutUtils.addHorizontalLabelledItem(content, "identifierDialog.version", versionField, gbc);
		pack();
	}

	@Override
	public void setVisible(boolean b) {
		setLocationRelativeTo(getOwner());
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		nameField.setText(contentPack.getIdentifier().getName());
		versionField.setText(contentPack.getIdentifier().getVersion().toString());
		super.setVisible(b);
	}
}
