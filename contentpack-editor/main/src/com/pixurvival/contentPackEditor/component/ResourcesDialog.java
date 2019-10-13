package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;

public class ResourcesDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	private JList<ResourceEntry> resourceList = new JList<>(new DefaultListModel<>());
	private JButton addButton = new CPEButton("generic.add", () -> ResourcesService.getInstance().addResource());
	private JButton modifyButton = new CPEButton("generic.modify", () -> {
		ResourceEntry entry = resourceList.getSelectedValue();
		if (entry != null) {
			ResourcesService.getInstance().addResource(entry.getName());
		}
	});
	private JButton importButton = new CPEButton("resources.importFolder", () -> ResourcesService.getInstance().importFolder());
	private JButton deleteButton = new CPEButton("generic.remove", this::remove);
	private JButton renameButton = new CPEButton("generic.rename", this::rename);
	private ResourcePreview resourcePreview = new ResourcePreview();

	public ResourcesDialog() {
		super("resourcesDialog.title");
		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		JScrollPane scrollPane = new JScrollPane(resourceList);
		JPanel listPanel = (JPanel) LayoutUtils.addBorder(scrollPane, 0, 0, 0, 0);
		JPanel buttonPanel = new JPanel();
		LayoutUtils.addVertically(buttonPanel, 1, GridBagConstraints.HORIZONTAL, addButton, modifyButton, renameButton, deleteButton, importButton);
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

	private void remove() {
		int selectedIndex = resourceList.getSelectedIndex();
		if (selectedIndex != -1) {
			ResourcesService.getInstance().deleteResource(resourceList.getSelectedValue().toString());
		}
	}

	private void rename() {
		int selectedIndex = resourceList.getSelectedIndex();
		if (selectedIndex != -1) {
			ResourcesService.getInstance().renameResource(resourceList.getSelectedValue().toString());
		}
	}
}
