package com.pixurvival.contentPackEditor.component.equipmentOffset;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetChooserPreviewTabs;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview.ClickEvent;
import com.pixurvival.contentPackEditor.component.util.ElementEditorTablePanel;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.NumberInput;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.FrameOffset;

public class EquipmentOffsetEditor extends RootElementEditor<EquipmentOffset> {

	private static final long serialVersionUID = 1L;

	NumberInput<Integer> widthInput = NumberInput.integerInput();
	NumberInput<Integer> heightInput = NumberInput.integerInput();
	private ElementEditorTablePanel<FrameOffset> tablePanel;
	private SpriteSheetChooserPreviewTabs previewPanel = new SpriteSheetChooserPreviewTabs();

	public EquipmentOffsetEditor() {

		// Construction
		tablePanel = new ElementEditorTablePanel<>((x, y) -> {
			FrameOffset frameOffset = new FrameOffset(x, y);
			FrameOffsetEditor editor = new FrameOffsetEditor();
			editor.setValue(frameOffset);
			return editor;
		});

		// Binding
		bind(tablePanel, EquipmentOffset::getFrameOffsets, EquipmentOffset::setFrameOffsets);

		widthInput.addValueChangeListener(x -> {
			tablePanel.setTableSize(x, heightInput.getValue() == null ? 0 : heightInput.getValue(), true);
		});
		heightInput.addValueChangeListener(y -> {
			tablePanel.setTableSize(widthInput.getValue() == null ? 0 : widthInput.getValue(), y, true);

		});
		previewPanel.getSpriteSheetPreview().addInteractionListener(o -> {
			if (o instanceof ClickEvent) {
				ClickEvent clickEvent = (ClickEvent) o;
				if (tablePanel.getTableWidth() > clickEvent.getSpriteX() && tablePanel.getTableHeight() > clickEvent.getSpriteY()) {
					FrameOffsetEditor editor = (FrameOffsetEditor) tablePanel.getCell(clickEvent.getSpriteX(), clickEvent.getSpriteY());
					editor.getValue().setOffsetX(clickEvent.getPixelX());
					editor.getValue().setOffsetY(clickEvent.getPixelY());
					editor.setValue(editor.getValue());
					editor.notifyValueChanged();
				}
			}
		});
		previewPanel.getSpriteSheetChooser().addValueChangeListener(spriteSheet -> {
			if (spriteSheet == null) {
				return;
			}
			ResourceEntry resourceEntry = ResourcesService.getInstance().getResource(spriteSheet.getImage());
			if (resourceEntry == null || !(resourceEntry.getPreview() instanceof BufferedImage)) {
				return;
			}
			BufferedImage image = (BufferedImage) resourceEntry.getPreview();
			int width = image.getWidth() / spriteSheet.getWidth();
			int height = image.getHeight() / spriteSheet.getHeight();
			if (width == tablePanel.getTableWidth() && height == tablePanel.getTableHeight()) {
				return;
			}
			int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString("equipmentOffsetEditor.setSizeToPreviewQuestion"));
			if (option == JOptionPane.YES_OPTION) {
				tablePanel.setTableSize(width, height, true);
			}
		});

		// Layouting
		JPanel sizesPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(sizesPanel, "generic.width", widthInput, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets.left = 5;
		LayoutUtils.addHorizontalLabelledItem(sizesPanel, "generic.height", heightInput, gbc);
		JPanel editionPanel = new JPanel(new BorderLayout());
		editionPanel.add(sizesPanel, BorderLayout.NORTH);
		editionPanel.add(tablePanel, BorderLayout.CENTER);
		setLayout(new BorderLayout());
		add(editionPanel, BorderLayout.WEST);
		add(previewPanel, BorderLayout.CENTER);
	}

	@Override
	protected void valueChanged() {
		SwingUtilities.invokeLater(() -> {
			widthInput.setValue(tablePanel.getTableWidth());
			heightInput.setValue(tablePanel.getTableHeight());
			previewPanel.getSpriteSheetPreview().setOverrideEquipmentOffset(getValue());
			previewPanel.repaint();
		});
	}
}
