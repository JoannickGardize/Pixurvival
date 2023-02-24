package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.trigger.EquippedTrigger;
import com.pixurvival.core.contentPack.item.trigger.Trigger;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public abstract class EquipablePanel extends ItemSpecificPartPanel {

    private static final long serialVersionUID = 1L;

    @AllArgsConstructor
    @Getter
    protected class Tab {
        private String title;
        private Component content;
    }

    private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
    private ListEditor<StatModifier> statModifiersEditor = new VerticalListEditor<>(StatModifierEditor::new, StatModifier::new, VerticalListEditor.HORIZONTAL);
    private ListEditor<Trigger> triggersEditor = new VerticalListEditor<>(TriggerEditor::new, EquippedTrigger::new, VerticalListEditor.VERTICAL);
    private JPanel parentPanel;
    private boolean showSpriteSheet;

    public EquipablePanel(boolean showSpriteSheet) {
        this.showSpriteSheet = showSpriteSheet;

        // Construction

        // Binding

        // Layouting

        statModifiersEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.statModifiers"));
        if (showSpriteSheet) {
            parentPanel = LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("elementType.spriteSheet", spriteSheetChooser), statModifiersEditor);
        } else {
            parentPanel = statModifiersEditor;
        }
    }

    public void bindTo(ItemEditor itemEditor, Class<? extends EquipableItem> type) {
        if (showSpriteSheet) {
            itemEditor.bind(spriteSheetChooser, "spriteSheet", type);
        }
        itemEditor.bind(statModifiersEditor, "statModifiers", EquipableItem.class);
        itemEditor.bind(triggersEditor, "triggers", EquipableItem.class);
    }

    protected void finalizeLayout(Collection<Tab> specificTabs) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(TranslationService.getInstance().getString("generic.general"), parentPanel);
        for (Tab tab : specificTabs) {
            tabbedPane.add(tab.getTitle(), tab.getContent());
        }
        tabbedPane.add(TranslationService.getInstance().getString("itemEditor.equipable.triggers"), triggersEditor);

        LayoutUtils.fill(this, tabbedPane);
    }

}
