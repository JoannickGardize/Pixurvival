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
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingRootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.DamageableStructure;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;

public class StructureEditor extends InstanceChangingRootElementEditor<Structure> {
	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);

	public StructureEditor() {
		super(Structure.class, "structureType");

		// Contruction
		BooleanCheckBox solidCheckBox = new BooleanCheckBox();
		BooleanCheckBox randomHorizontalFlipCheckBox = new BooleanCheckBox();
		BooleanCheckBox avoidStuckCheckBox = new BooleanCheckBox();
		DimensionsEditor dimensionsEditor = new DimensionsEditor();
		FloatInput lightEmissionRadiusInput = new FloatInput();
		TimeInput durationInput = new TimeInput();
		TimeInput deconstructionDuration = new TimeInput();
		ListEditor<Tile> bannedTilesEditor = new HorizontalListEditor<>(() -> {
			ElementChooserButton<Tile> tileChooser = new ElementChooserButton<>(Tile.class);
			tileChooser.setBorder(LayoutUtils.createBorder());
			return tileChooser;
		}, () -> null);

		// Binding
		bind(randomHorizontalFlipCheckBox, "randomHorizontalFlip");
		bind(avoidStuckCheckBox, "avoidStuck");
		bind(solidCheckBox, "solid");
		bind(spriteSheetChooser, "spriteSheet");
		bind(dimensionsEditor, "dimensions");
		bind(durationInput, "duration");
		bind(bannedTilesEditor, "bannedTiles");
		bind(lightEmissionRadiusInput, "lightEmissionRadius");
		bind(deconstructionDuration, "deconstructionDuration");

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
		gbc.gridwidth = 2;
		northPanel.add(dimensionsEditor, gbc);
		gbc.gridy = 4;
		gbc.weighty = 0;
		LayoutUtils.addHorizontalLabelledItem(northPanel, "structureEditor.deconstructionDuration", deconstructionDuration, gbc);
		LayoutUtils.nextColumn(gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.solid", solidCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "elementType.spriteSheet", spriteSheetChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "structureEditor.randomHorizontalFlip", randomHorizontalFlipCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "structureEditor.avoidStuck", avoidStuckCheckBox, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.duration", "structureEditor.duration.tooltip", durationInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "structureEditor.lightEmissionRadius", "structureEditor.lightEmissionRadius.tooltip", lightEmissionRadiusInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(northPanel, "generic.type", getTypeChooser(), gbc);

		bannedTilesEditor.setBorder(LayoutUtils.createGroupBorder("structureEditor.bannedTiles"));
		LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 2, northPanel, bannedTilesEditor, getSpecificPartPanel());
	}

	@Override
	protected List<ClassEntry> getClassEntries(Object params) {
		List<ClassEntry> entries = new ArrayList<>();

		entries.add(new ClassEntry(Structure.class, JPanel::new));

		entries.add(new ClassEntry(HarvestableStructure.class, () -> {
			HarvestablePanel harvestablePanel = new HarvestablePanel();
			harvestablePanel.bindTo(this);
			return harvestablePanel;
		}));

		entries.add(new ClassEntry(DamageableStructure.class, () -> {
			FloatInput healthInput = new FloatInput();
			bind(healthInput, "maxHealth", DamageableStructure.class);
			return LayoutUtils.single(LayoutUtils.labelled("structureEditor.damageable.health", healthInput));
		}));

		return entries;
	}
}
