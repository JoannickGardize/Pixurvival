package com.pixurvival.contentPackEditor.component.structure;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;
import com.pixurvival.contentPackEditor.component.valueComponent.HorizontalListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.structure.FactoryCraft;
import com.pixurvival.core.contentPack.structure.FactoryFuel;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.contentPack.structure.StructureDeathItemHandling;

public class FactoryStructurePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FactoryStructurePanel(StructureEditor structureEditor) {
		ElementChooserButton<SpriteSheet> workingSpriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
		EnumChooser<StructureDeathItemHandling> itemHandlingOnDeathChooser = new EnumChooser<>(StructureDeathItemHandling.class, "playerDeathItemHandling");

		IntegerInput recipeSizeInput = new IntegerInput();
		IntegerInput fuelSizeInput = new IntegerInput();
		IntegerInput resultSizeInput = new IntegerInput();

		ListEditor<FactoryFuel> fuelsEditor = new HorizontalListEditor<>(FactoryFuelEditor::new, FactoryFuel::new);
		ListEditor<FactoryCraft> craftsEditor = new VerticalListEditor<>(FactoryCraftEditor::new, FactoryCraft::new);

		structureEditor.bind(itemHandlingOnDeathChooser, "itemHandlingOnDeath", FactoryStructure.class);
		structureEditor.bind(workingSpriteSheetChooser, "workingSpriteSheet", FactoryStructure.class);
		structureEditor.bind(recipeSizeInput, "recipeSize", FactoryStructure.class);
		structureEditor.bind(fuelSizeInput, "fuelSize", FactoryStructure.class);
		structureEditor.bind(resultSizeInput, "resultSize", FactoryStructure.class);
		structureEditor.bind(fuelsEditor, "fuels", FactoryStructure.class);
		structureEditor.bind(craftsEditor, "crafts", FactoryStructure.class);

		JPanel inventoryPanel = LayoutUtils.createHorizontalLabelledBox("factoryStructureEditor.recipeSize", LayoutUtils.single(recipeSizeInput), "factoryStructureEditor.fuelSize",
				LayoutUtils.single(fuelSizeInput), "factoryStructureEditor.resultSize", LayoutUtils.single(resultSizeInput));

		inventoryPanel.setBorder(LayoutUtils.createGroupBorder("factoryStructureEditor.inventorySize"));
		fuelsEditor.setBorder(LayoutUtils.createGroupBorder("factoryStructureEditor.fuels"));
		craftsEditor.setBorder(LayoutUtils.createGroupBorder("factoryStructureEditor.crafts"));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(LayoutUtils.createHorizontalLabelledBox("factoryStructureEditor. workingSpriteSheet", LayoutUtils.single(workingSpriteSheetChooser), "structureEditor.itemHandlingOnDeath",
				LayoutUtils.single(itemHandlingOnDeathChooser)));
		add(inventoryPanel);
		add(fuelsEditor);
		add(craftsEditor);
	}

}
