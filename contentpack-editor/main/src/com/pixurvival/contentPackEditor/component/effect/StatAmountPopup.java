package com.pixurvival.contentPackEditor.component.effect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.util.RelativePopup;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.alteration.StatAmount;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;
import com.pixurvival.core.livingEntity.stats.StatType;

import lombok.Setter;

public class StatAmountPopup extends RelativePopup {

	private static final long serialVersionUID = 1L;

	private FloatInput baseInput;
	private FloatInput[] statMultiplierInputs;
	private StatAmount value;
	private @Setter Runnable onCloseAction = () -> {
	};

	public StatAmountPopup() {
		baseInput = new FloatInput();
		baseInput.setColumns(6);
		statMultiplierInputs = new FloatInput[StatType.values().length];
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(getContentPane(), "generic.base", baseInput, gbc);
		for (int i = 0; i < StatType.values().length; i++) {
			statMultiplierInputs[i] = new FloatInput();
			statMultiplierInputs[i].setColumns(6);
			LayoutUtils.addHorizontalLabelledItem(getContentPane(), "+ " + TranslationService.getInstance().getString(StatType.values()[i]) + " x", false, null, statMultiplierInputs[i], gbc);
		}
		pack();
	}

	public void setValue(StatAmount value) {
		this.value = value;
		if (value == null) {
			this.value = new StatAmount();
		}
		baseInput.setValue(this.value.getBase());
		for (int i = 0; i < StatType.values().length; i++) {
			statMultiplierInputs[i].setValue(0f);
		}
		for (StatMultiplier multiplier : this.value.getStatMultipliers()) {
			FloatInput input = statMultiplierInputs[multiplier.getStatType().ordinal()];
			input.setValue(input.getValue() + multiplier.getMultiplier());
		}
	}

	public StatAmount getValue() {
		if (this.value == null) {
			value = new StatAmount();
		}
		value.setBase(baseInput.getValue());
		value.getStatMultipliers().clear();
		for (int i = 0; i < StatType.values().length; i++) {
			if (statMultiplierInputs[i].getValue() != 0) {
				value.getStatMultipliers().add(new StatMultiplier(StatType.values()[i], statMultiplierInputs[i].getValue()));
			}
		}
		return value;
	}

	@Override
	protected void onClose() {
		onCloseAction.run();
	}
}