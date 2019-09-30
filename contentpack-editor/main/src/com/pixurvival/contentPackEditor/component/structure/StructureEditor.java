package com.pixurvival.contentPackEditor.component.structure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DimensionsEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructureEditor extends InstanceChangingRootElementEditor<Structure> {
	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class, LayoutUtils.getSpriteSheetIconProvider());

	public StructureEditor() {
		super("structureType");

		// Contruction
		BooleanCheckBox solidCheckBox = new BooleanCheckBox();
		DimensionsEditor dimensionsEditor = new DimensionsEditor();
		DoubleInput lightEmissionRadiusInput = new DoubleInput(Bounds.positive());
		TimeInput durationInput = new TimeInput();
		ListEditor<Tile> bannedTilesEditor = new HorizontalListEditor<>(() -> {
			ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(Tile.class, IconService.getInstance()::get, true);
			tileChooser.setBorder(LayoutUtils.createBorder());
			return tileChooser;
		}, () -> null);

		// Binding

		bind(solidCheckBox, Structure::isSolid, Structure::setSolid);
		bind(spriteSheetChooser, Structure::getSpriteSheet, Structure::setSpriteSheet);
		bind(dimensionsEditor, Structure::getDimensions, Structure::setDimensions);
		bind(durationInput, Structure::getDuration, Structure::setDuration);
		bind(bannedTilesEditor, Structure::getBannedTiles, Structure::setBannedTiles);
		bind(lightEmissionRadiusInput, Structure::getLightEmissionRadius, Structure::setLightEmissionRadius);

		// Layouting
		setLayout(new GridBagLayout());
		getSpecificPartPanel().setBorder(LayoutUtils.createGroupBorder("generic.typeProperties"));

		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(LayoutUtils.createGroupBorder("generic.generalProperties"));
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.gridheight = 4;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets.top = 4;
		gbc.insets.bottom = 4;
		northPanel.add(dimensionsEditor, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.solid", solidCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.duration", "structureEditor.duration.tooltip", durationInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "structureEditor.lightEmissionRadius", "structureEditor.lightEmissionRadius.tooltip", lightEmissionRadiusInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", getTypeChooser(), gbc);

		bannedTilesEditor.setBorder(LayoutUtils.createGroupBorder("structureEditor.bannedTiles"));
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 2, northPanel, bannedTilesEditor, getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries() {
		List<ClassEntry> entries = new ArrayList<>();

		entries.add(new ClassEntry(Structure.class, new JPanel()));

		HarvestablePanel harvestablePanel = new HarvestablePanel();
		harvestablePanel.bindTo(this);
		entries.add(new ClassEntry(HarvestableStructure.class, harvestablePanel));

		return entries;
	}
}
