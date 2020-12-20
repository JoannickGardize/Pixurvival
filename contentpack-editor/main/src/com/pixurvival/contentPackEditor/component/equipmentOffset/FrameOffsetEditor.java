package com.pixurvival.contentPackEditor.component.equipmentOffset;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.IntegerInput;
import com.pixurvival.core.contentPack.sprite.FrameOffset;

public class FrameOffsetEditor extends ElementEditor<FrameOffset> {

	private static final long serialVersionUID = 1L;

	public FrameOffsetEditor() {
		super(FrameOffset.class);

		// Construction
		setBorder(BorderFactory.createEtchedBorder());
		IntegerInput offsetXInput = new IntegerInput();
		IntegerInput offsetYInput = new IntegerInput();
		BooleanCheckBox isBackCheckbox = new BooleanCheckBox();

		// Binding
		bind(offsetXInput, "offsetX");
		bind(offsetYInput, "offsetY");
		bind(isBackCheckbox, "back");

		// Layouting
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "generic.x", offsetXInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "generic.y", offsetYInput, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "equipmentOffsetEditor.isBack", isBackCheckbox, gbc);

	}
}
