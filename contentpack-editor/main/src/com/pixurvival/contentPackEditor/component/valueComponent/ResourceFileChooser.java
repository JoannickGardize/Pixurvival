package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.ResourcePreview;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

/**
 * Special Value component that doesn't change any value, instead, it allows to
 * choose file for an associated resource, and throw a value changing event when
 * the resource change.
 * 
 * @author SharkHendrix
 *
 */
public class ResourceFileChooser extends JPanel implements ValueComponent<String> {

	private static final long serialVersionUID = 1L;

	private List<ValueChangeListener<String>> listeners = new ArrayList<>();
	private String resourceName;
	private ResourcePreview resourcePreview = new ResourcePreview();

	private JButton importButton = new CPEButton("mapGeneratorEditor.importImage", () -> {
		if (resourceName != null) {
			ResourcesService.getInstance().addResource(resourceName);
			update();
			listeners.forEach(l -> l.valueChanged(resourceName));
		}
	});

	public ResourceFileChooser() {
		resourcePreview.setPreferredSize(new Dimension(300, 200));
		JPanel previewPanel = LayoutUtils.single(resourcePreview);
		previewPanel.setBorder(LayoutUtils.createGroupBorder("generic.preview"));
		add(LayoutUtils.createHorizontalBox(1, LayoutUtils.single(importButton), previewPanel));
	}

	@Override
	public String getValue() {
		return resourceName;
	}

	@Override
	public void setValue(String value) {
		resourceName = value;
		update();
	}

	@Override
	public boolean isValueValid(String value) {
		return ResourcesService.getInstance().containsResource(resourceName);
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		// Standalone component
	}

	@Override
	public JLabel getAssociatedLabel() {
		return null;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<String> listener) {
		listeners.add(listener);
	}

	private void update() {
		ResourceEntry entry = ResourcesService.getInstance().getResource(resourceName);
		if (entry == null) {
			resourcePreview.setObject(null);
			importButton.setForeground(Color.RED);
		} else {
			resourcePreview.setObject(entry.getPreview());
			importButton.setForeground(Color.BLACK);
		}
		resourcePreview.repaint();

	}
}
