package com.pixurvival.contentPackEditor.component.util;

import java.awt.GridBagConstraints;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WeightX extends LayoutPropertyMarker {
	private float value;

	@Override
	public void apply(GridBagConstraints gbc) {
		gbc.weightx = value;
	}
}
