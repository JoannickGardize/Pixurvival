package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.stats.StatModifier;

public abstract class EquipablePanel extends ItemSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(SpriteSheet.class);
	private ListEditor<StatModifier> statModifiersEditor = new VerticalListEditor<>(StatModifierEditor::new, StatModifier::new, VerticalListEditor.HORIZONTAL);
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
	}

	protected void finalizeLayout(JPanel childPanel) {
		LayoutUtils.addSideBySide(this, parentPanel, childPanel);
	}

}
