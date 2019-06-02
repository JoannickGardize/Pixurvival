package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.stats.StatModifier;

public abstract class EquipablePanel extends ItemSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());
	private ListEditor<StatModifier> statModifiersEditor = new VerticalListEditor<>(StatModifierEditor::new, StatModifier::new);
	private JPanel parentPanel;

	public EquipablePanel() {
		EventManager.getInstance().register(this);

		// Construction

		// Binding

		// Layouting

		statModifiersEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.statModifiers"));
		parentPanel = LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("elementType.spriteSheet", spriteSheetChooser), statModifiersEditor);
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		itemEditor.bind(spriteSheetChooser, EquipableItem::getSpriteSheet, EquipableItem::setSpriteSheet, EquipableItem.class);
		itemEditor.bind(statModifiersEditor, EquipableItem::getStatModifiers, EquipableItem::setStatModifiers, EquipableItem.class);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

	protected void finalizeLayout(JPanel childPanel) {
		LayoutUtils.addSideBySide(this, parentPanel, childPanel);
	}

}
