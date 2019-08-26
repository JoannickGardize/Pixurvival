package com.pixurvival.contentPackEditor.component.effect;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueChangeListener;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.livingEntity.alteration.StatAmount;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;

import lombok.Getter;
import lombok.Setter;

public class StatAmountEditor extends JButton implements ValueComponent<StatAmount> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter JLabel associatedLabel;
	private List<ValueChangeListener<StatAmount>> listeners = new ArrayList<>();
	private StatAmount value;

	private StatAmountPopup popup = new StatAmountPopup();

	public StatAmountEditor() {
		popup.setOnCloseAction(() -> setValue(popup.getValue()));
		addActionListener(e -> {
			popup.setValue(value);
			popup.show(this);
		});
	}

	@Override
	public StatAmount getValue() {
		return value;
	}

	@Override
	public void setValue(StatAmount value) {
		this.value = value;
		updateButtonText();
	}

	@Override
	public boolean isValueValid(StatAmount value) {
		return value != null;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<StatAmount> listener) {
		listeners.add(listener);
	}

	private void updateButtonText() {
		if (value == null) {
			setText("??");
			return;
		}
		StringBuilder sb = new StringBuilder(Float.toString(value.getBase()));
		if (value.getStatMultipliers() != null) {
			for (StatMultiplier multiplier : value.getStatMultipliers()) {
				sb.append(" + ");
				sb.append(TranslationService.getInstance().getString(multiplier.getStatType()));
				sb.append(" x ");
				sb.append(multiplier.getMultiplier());
			}
		}
		setText(sb.toString());
	}

}
