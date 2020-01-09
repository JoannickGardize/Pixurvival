package com.pixurvival.contentPackEditor.component.gameMode;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;

public class MapLimitsAnchorEditor extends ElementEditor<MapLimitsAnchor> {

	private static final long serialVersionUID = 1L;

	public MapLimitsAnchorEditor() {
		TimeInput timeInput = new TimeInput();
		FloatInput sizeInput = new FloatInput(Bounds.positive());
		FloatInput damageInput = new FloatInput(Bounds.positive());

		bind(timeInput, MapLimitsAnchor::getTime, MapLimitsAnchor::setTime);
		bind(sizeInput, MapLimitsAnchor::getSize, MapLimitsAnchor::setSize);
		bind(damageInput, MapLimitsAnchor::getDamagePerSecond, MapLimitsAnchor::setDamagePerSecond);

		JPanel topPanel = LayoutUtils.createHorizontalLabelledBox("generic.time", timeInput);
		JPanel bottomPanel = LayoutUtils.createHorizontalLabelledBox("generic.size", sizeInput, "mapLimits.damagePerSecond", damageInput);
		add(LayoutUtils.createVerticalBox(topPanel, bottomPanel));
	}
}
