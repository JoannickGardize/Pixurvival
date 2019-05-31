package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.ChangeableTypeBuilder;
import com.pixurvival.contentPackEditor.component.valueComponent.ChangeableTypeEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.DimensionsEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

public class StructureEditor extends RootElementEditor<Structure> {
	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());
	private JComboBox<Class<? extends Structure.Details>> typeChooser;

	public StructureEditor() {

		// Contruction
		ChangeableTypeEditor<Structure.Details> detailsEditor;
		BooleanCheckBox solidCheckBox = new BooleanCheckBox();
		DimensionsEditor dimensionsEditor = new DimensionsEditor();
		ChangeableTypeBuilder<Structure.Details> builder = new ChangeableTypeBuilder<>(Structure.class, getClass().getPackage().getName(), "structure.type");
		typeChooser = builder.getChooser();
		detailsEditor = builder.getEditor();

		// Binding

		bind(solidCheckBox, Structure::isSolid, Structure::setSolid);
		bind(spriteSheetChooser, Structure::getSpriteSheet, Structure::setSpriteSheet);
		bind(dimensionsEditor, Structure::getDimensions, Structure::setDimensions);
		bind(detailsEditor, Structure::getDetails, Structure::setDetails);

		// Layouting
		setLayout(new GridBagLayout());
		detailsEditor.setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));

		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(LayoutUtils.createGroupBorder("generic.generalProperties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.gridheight = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets.top = 4;
		gbc.insets.bottom = 4;
		northPanel.add(dimensionsEditor, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.solid", solidCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", typeChooser, gbc);

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		add(detailsEditor, gbc);

	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (source == this) {
			typeChooser.setSelectedItem(((Structure) source.getValue()).getDetails().getClass());
		}
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}
}
