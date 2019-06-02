package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.DimensionsEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.OrnamentalStructure;
import com.pixurvival.core.contentPack.structure.ShortLivedStructure;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructureEditor extends InstanceChangingRootElementEditor<Structure> {
	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());

	public StructureEditor() {
		super("structureType");

		// Contruction
		BooleanCheckBox solidCheckBox = new BooleanCheckBox();
		DimensionsEditor dimensionsEditor = new DimensionsEditor();

		// Binding

		bind(solidCheckBox, Structure::isSolid, Structure::setSolid);
		bind(spriteSheetChooser, Structure::getSpriteSheet, Structure::setSpriteSheet);
		bind(dimensionsEditor, Structure::getDimensions, Structure::setDimensions);

		// Layouting
		setLayout(new GridBagLayout());
		getSpecificPartPanel().setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));

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
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", getTypeChooser(), gbc);

		gbc = LayoutUtils.createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(northPanel, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		add(getSpecificPartPanel(), gbc);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		entries.add(new ClassEntry(OrnamentalStructure.class, new JPanel()));

		HarvestablePanel harvestablePanel = new HarvestablePanel();
		harvestablePanel.bindTo(this);
		entries.add(new ClassEntry(HarvestableStructure.class, harvestablePanel));

		ShortLivedPanel shortLivedPanel = new ShortLivedPanel();
		shortLivedPanel.bindTo(this);
		entries.add(new ClassEntry(ShortLivedStructure.class, shortLivedPanel));

		return entries;
	}
}
