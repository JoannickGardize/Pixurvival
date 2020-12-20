package com.pixurvival.contentPackEditor.component.gameMode;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;

public class MapLimitsAnchorEditor extends ElementEditor<MapLimitsAnchor> {

	private static final long serialVersionUID = 1L;

	public MapLimitsAnchorEditor() {
		super(MapLimitsAnchor.class);
		TimeInput timeInput = new TimeInput();
		FloatInput sizeInput = new FloatInput();
		FloatInput damageInput = new FloatInput();

		bind(timeInput, "time");
		bind(sizeInput, "size");
		bind(damageInput, "damagePerSecond");

		JPanel topPanel = LayoutUtils.createHorizontalLabelledBox("generic.time", timeInput);
		JPanel bottomPanel = LayoutUtils.createHorizontalLabelledBox("generic.size", sizeInput, "mapLimits.damagePerSecond", damageInput);
		add(LayoutUtils.createVerticalBox(topPanel, bottomPanel));
	}
}
