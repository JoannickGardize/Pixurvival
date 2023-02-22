package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.core.alteration.DamageAttributes;

import java.awt.*;

public class DamageAttributesEditor extends ElementEditor<DamageAttributes> {

    private static final long serialVersionUID = 1L;

    public DamageAttributesEditor() {
        super(DamageAttributes.class);
        BooleanCheckBox bypassInvincibilityCheckBox = new BooleanCheckBox();
        BooleanCheckBox trueDamageCheckBox = new BooleanCheckBox();

        bind(bypassInvincibilityCheckBox, "bypassInvincibility");
        bind(trueDamageCheckBox, "trueDamage");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(this, "damageAttributes.bypassInvincibility", bypassInvincibilityCheckBox, gbc);
        LayoutUtils.addHorizontalLabelledItem(this, "damageAttributes.trueDamage", trueDamageCheckBox, gbc);
    }
}
