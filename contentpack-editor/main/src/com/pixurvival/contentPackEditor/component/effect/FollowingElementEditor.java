package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.abilitySet.OffsetAngleEffectEditor;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.BooleanCheckBox;
import com.pixurvival.contentPackEditor.component.valueComponent.InstanceChangingElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.StatFormulaEditor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.FollowingCreature;
import com.pixurvival.core.contentPack.effect.FollowingEffect;
import com.pixurvival.core.contentPack.effect.FollowingElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FollowingElementEditor extends InstanceChangingElementEditor<FollowingElement> {

    private static final long serialVersionUID = 1L;

    public FollowingElementEditor(Object... topLineLabelAndComponents) {
        super(FollowingElement.class, "followingElementType", null);

        setLayout(new BorderLayout());
        int length = topLineLabelAndComponents.length;
        Object[] fullLabelAndComponents = Arrays.copyOf(topLineLabelAndComponents, topLineLabelAndComponents.length + 2);
        fullLabelAndComponents[length] = "generic.type";
        fullLabelAndComponents[length + 1] = getTypeChooser();
        add(LayoutUtils.createHorizontalLabelledBox(fullLabelAndComponents), BorderLayout.NORTH);
        add(getSpecificPartPanel(), BorderLayout.CENTER);
    }

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();

        // FollowingEffect
        entries.add(new ClassEntry(FollowingEffect.class, () -> {
            OffsetAngleEffectEditor offsetAngleEffectEditor = new OffsetAngleEffectEditor();
            bind(offsetAngleEffectEditor, "offsetAngleEffect", FollowingEffect.class);
            return offsetAngleEffectEditor;
        }));

        // FollowingCreature
        entries.add(new ClassEntry(FollowingCreature.class, () -> {
            ElementChooserButton<Creature> creatureChooser = new ElementChooserButton<>(Creature.class);
            BooleanCheckBox ownedCheckBox = new BooleanCheckBox();
            StatFormulaEditor strengthEditor = new StatFormulaEditor();
            StatFormulaEditor agilityEditor = new StatFormulaEditor();
            StatFormulaEditor intelligenceEditor = new StatFormulaEditor();
            bind(creatureChooser, "creature", FollowingCreature.class);
            bind(ownedCheckBox, "owned", FollowingCreature.class);
            bind(strengthEditor, "strengthBonus", FollowingCreature.class);
            bind(agilityEditor, "agilityBonus", FollowingCreature.class);
            bind(intelligenceEditor, "intelligenceBonus", FollowingCreature.class);
            JPanel statPanel = LayoutUtils.createVerticalLabelledBox("statType.strength", strengthEditor, "statType.agility", agilityEditor, "statType.intelligence", intelligenceEditor);
            statPanel.setBorder(LayoutUtils.createGroupBorder("followingCreatureEditor.bonusStats"));
            return LayoutUtils.createVerticalBox(LayoutUtils.createHorizontalLabelledBox("elementType.creature", creatureChooser, "followingCreatureEditor.owned", ownedCheckBox), statPanel);
        }));

        return entries;
    }

    @Override
    protected void initialize(FollowingElement oldInstance, FollowingElement newInstance) {
    }

}
