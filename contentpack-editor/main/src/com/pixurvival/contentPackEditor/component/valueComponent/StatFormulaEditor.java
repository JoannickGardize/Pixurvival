package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.alteration.StatMultiplier;

import lombok.Getter;
import lombok.Setter;

public class StatFormulaEditor extends JButton implements ValueComponent<StatFormula> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter JLabel associatedLabel;
	private List<ValueChangeListener<StatFormula>> listeners = new ArrayList<>();
	private StatFormula value;

	private StatFormulaPopup popup = new StatFormulaPopup();

	public StatFormulaEditor() {
		popup.setOnCloseAction(() -> setValue(popup.getValue()));
		addActionListener(e -> {
			popup.setValue(value);
			popup.show(this);
		});
	}

	@Override
	public StatFormula getValue() {
		return value;
	}

	@Override
	public void setValue(StatFormula value) {
		this.value = value;
		if (value.getId() == -1) {
			// Initialize the formula id to be unique
			value.setId(ContentPackEditionService.getInstance().nextStatFormulaId());
		}
		updateButtonText();
	}

	@Override
	public boolean isValueValid(StatFormula value) {
		return value != null;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<StatFormula> listener) {
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
		if (value.getRandomValue() != 0) {
			sb.append(" + ");
			sb.append(TranslationService.getInstance().getString("statFormulaEditor.random"));
			sb.append(" [0 ; ");
			sb.append(value.getRandomValue());
			sb.append("]");
		}
		if (sb.length() > 100) {
			sb.setLength(97);
			sb.append("...");
		}
		setText(sb.toString());
	}

}
