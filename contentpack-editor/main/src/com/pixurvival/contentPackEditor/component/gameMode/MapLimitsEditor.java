package com.pixurvival.contentPackEditor.component.gameMode;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;

public class MapLimitsEditor extends ElementEditor<MapLimits> {

	private static final long serialVersionUID = 1L;

	private CardLayout cardLayout = new CardLayout();
	private JPanel notNullPanel = new JPanel(cardLayout);

	private BooleanCheckBox enableMapLimitsCheckBox = new BooleanCheckBox();

	public MapLimitsEditor() {
		BooleanCheckBox shrinkRandomlyCheckBox = new BooleanCheckBox();
		FloatInput initialSizeInput = new FloatInput(Bounds.positive());
		FloatInput initialDamageInput = new FloatInput(Bounds.positive());
		ListEditor<MapLimitsAnchor> anchors = new VerticalListEditor<>(MapLimitsAnchorEditor::new, MapLimitsAnchor::new);

		bind(shrinkRandomlyCheckBox, MapLimits::isShrinkRandomly, MapLimits::setShrinkRandomly);
		bind(initialSizeInput, MapLimits::getInitialSize, MapLimits::setInitialSize);
		bind(initialDamageInput, MapLimits::getInitialDamagePerSecond, MapLimits::setInitialDamagePerSecond);
		bind(anchors, MapLimits::getAnchors, MapLimits::setAnchors);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("mapLimits.shrinkRandomly", shrinkRandomlyCheckBox),
				LayoutUtils.createHorizontalLabelledBox("mapLimits.initialSize", initialSizeInput, "mapLimits.initialDamagePerSecond", initialDamageInput)), BorderLayout.NORTH);
		contentPanel.add(anchors, BorderLayout.CENTER);

		notNullPanel.add(contentPanel, "NOT_NULL");
		notNullPanel.add(new JPanel(), "NULL");

		setLayout(new BorderLayout());
		add(LayoutUtils.labelled("mapLimits.enabled", enableMapLimitsCheckBox), BorderLayout.NORTH);
		add(notNullPanel, BorderLayout.CENTER);

		enableMapLimitsCheckBox.addActionListener(e -> {
			boolean checked = enableMapLimitsCheckBox.isSelected();
			if (checked && getValue() == null) {
				setValue(new MapLimits());
				notifyValueChanged();
			} else if (!checked && getValue() != null) {
				setValue(null);
				notifyValueChanged();
			}
			if (checked) {
				cardLayout.show(notNullPanel, "NOT_NULL");
			} else {
				cardLayout.show(notNullPanel, "NULL");
			}
		});
		cardLayout.show(notNullPanel, "NULL");
	}

	@Override
	protected void valueChanged(ValueComponent<?> source) {
		if (getValue() == null) {
			enableMapLimitsCheckBox.setSelected(false);
			cardLayout.show(notNullPanel, "NULL");
		} else {
			enableMapLimitsCheckBox.setSelected(true);
			cardLayout.show(notNullPanel, "NOT_NULL");
		}
	}

	@Override
	protected boolean isNullable() {
		return true;
	}
}
