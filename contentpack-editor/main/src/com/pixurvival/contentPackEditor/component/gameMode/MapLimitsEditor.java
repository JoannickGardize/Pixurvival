package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.NullableElementHelper;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;

public class MapLimitsEditor extends ElementEditor<MapLimits> {

	private static final long serialVersionUID = 1L;

	private NullableElementHelper<MapLimits> nullableElementHelper = new NullableElementHelper<>(this);

	public MapLimitsEditor() {
		BooleanCheckBox shrinkRandomlyCheckBox = new BooleanCheckBox();
		FloatInput initialSizeInput = new FloatInput(Bounds.positive());
		FloatInput initialDamageInput = new FloatInput(Bounds.positive());
		ListEditor<MapLimitsAnchor> anchors = new VerticalListEditor<>(MapLimitsAnchorEditor::new, MapLimitsAnchor::new);

		bind(shrinkRandomlyCheckBox, MapLimits::isShrinkRandomly, MapLimits::setShrinkRandomly);
		bind(initialSizeInput, MapLimits::getInitialSize, MapLimits::setInitialSize);
		bind(initialDamageInput, MapLimits::getInitialDamagePerSecond, MapLimits::setInitialDamagePerSecond);
		bind(anchors, MapLimits::getAnchors, MapLimits::setAnchors);

		JPanel contentPanel = nullableElementHelper.getNotNullPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("mapLimits.shrinkRandomly", shrinkRandomlyCheckBox),
				LayoutUtils.createHorizontalLabelledBox("mapLimits.initialSize", initialSizeInput, "mapLimits.initialDamagePerSecond", initialDamageInput)), BorderLayout.NORTH);
		contentPanel.add(anchors, BorderLayout.CENTER);

		nullableElementHelper.build(MapLimits::new);
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		nullableElementHelper.onValueChanged();
	}

	@Override
	public boolean isNullable() {
		return true;
	}
}
