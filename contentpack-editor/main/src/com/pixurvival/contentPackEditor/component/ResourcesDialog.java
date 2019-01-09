package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;

public class ResourcesDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	private JList<ResourceEntry> resourceList = new JList<>(new DefaultListModel<>());
	private JButton addButton = new CPEButton("generic.add", () -> ResourcesService.getInstance().addResource());
	private JButton importButton = new CPEButton("resources.importFolder",
			() -> ResourcesService.getInstance().importFolder());
	private ResourcePreview resourcePreview = new ResourcePreview();

	public ResourcesDialog() {
		super("resourcesDialog.title");
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		JPanel listPanel = new JPanel(new BorderLayout());

		JScrollPane scrollPane = new JScrollPane(resourceList);
		listPanel.add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(addButton, gbc);
		gbc.gridy++;
		buttonPanel.add(importButton, gbc);
		listPanel.add(buttonPanel, BorderLayout.SOUTH);

		content.add(listPanel, BorderLayout.WEST);
		content.add(resourcePreview, BorderLayout.CENTER);

		resourceList.addListSelectionListener(e -> {
			if (resourceList.getSelectedValue() == null) {
				resourcePreview.setObject(null);
			} else {
				resourcePreview.setObject(resourceList.getSelectedValue().getPreview());
			}
			resourcePreview.repaint();
		});

		EventManager.getInstance().register(this);
		pack();
	}

	@EventListener
	public void resourceListChanged(ResourceListChangedEvent event) {
		DefaultListModel<ResourceEntry> model = (DefaultListModel<ResourceEntry>) resourceList.getModel();
		model.clear();
		for (ResourceEntry entry : ResourcesService.getInstance().getResources()) {
			model.addElement(entry);
		}
	}

	@Override
	public void setVisible(boolean b) {
		setLocationRelativeTo(getOwner());
		super.setVisible(b);
	}
}
