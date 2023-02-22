package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.util.RelativePopup;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.alteration.StatMultiplier;
import com.pixurvival.core.livingEntity.stats.StatType;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class StatFormulaPopup extends RelativePopup {

    private static final long serialVersionUID = 1L;

    private FloatInput baseInput;
    private FloatInput randomInput;
    private FloatInput[] statMultiplierInputs;
    private JTextField idField = new JTextField();
    private StatFormula value;
    private @Setter Runnable onCloseAction = () -> {
    };

    public StatFormulaPopup() {
        baseInput = new FloatInput();
        baseInput.setColumns(6);
        randomInput = new FloatInput();
        randomInput.setColumns(6);
        statMultiplierInputs = new FloatInput[StatType.values().length];
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(getContentPane(), "generic.base", baseInput, gbc);
        for (int i = 0; i < StatType.values().length; i++) {
            statMultiplierInputs[i] = new FloatInput();
            statMultiplierInputs[i].setColumns(6);
            LayoutUtils.addHorizontalLabelledItem(getContentPane(), "+ " + TranslationService.getInstance().getString(StatType.values()[i]) + " x", false, null,
                    statMultiplierInputs[i], gbc);
        }
        LayoutUtils.addHorizontalLabelledItem(getContentPane(), "statFormulaPopup.random", randomInput, gbc);
        idField.setEditable(false);
        LayoutUtils.addHorizontalLabelledItem(getContentPane(), "generic.id", idField, gbc);
        pack();
    }

    public void setValue(StatFormula value) {
        this.value = value;
        if (value == null) {
            this.value = new StatFormula();
        }
        baseInput.setValue(this.value.getBase());
        for (int i = 0; i < StatType.values().length; i++) {
            statMultiplierInputs[i].setValue(0f);
        }
        for (StatMultiplier multiplier : this.value.getStatMultipliers()) {
            FloatInput input = statMultiplierInputs[multiplier.getStatType().ordinal()];
            input.setValue(input.getValue() + multiplier.getMultiplier());
        }
        randomInput.setValue(this.value.getRandomValue());
        idField.setText(String.valueOf(value.getId()));
    }

    public StatFormula getValue() {
        if (this.value == null) {
            value = new StatFormula();
        }
        value.setBase(baseInput.getValue());
        value.getStatMultipliers().clear();
        for (int i = 0; i < StatType.values().length; i++) {
            if (statMultiplierInputs[i].getValue() != 0) {
                value.getStatMultipliers().add(new StatMultiplier(StatType.values()[i], statMultiplierInputs[i].getValue()));
            }
        }
        value.setRandomValue(randomInput.getValue());
        return value;
    }

    @Override
    protected void onClose() {
        onCloseAction.run();
    }
}
