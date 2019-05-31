package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.EquipableItem;
import com.pixurvival.core.livingEntity.stats.StatModifier;

import lombok.Getter;

public abstract class EquipableEditor<T extends EquipableItem> extends ElementEditor<T> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());

	private JPanel leftPanel;
	private @Getter JPanel rightPanel = new JPanel();

	public EquipableEditor() {
		EventManager.getInstance().register(this);

		// Construction

		ListEditor<StatModifier> alterationListEditor = new VerticalListEditor<>(StatModifierEditor::new, StatModifier::new);

		// Binding

		bind(spriteSheetChooser, EquipableItem::getSpriteSheet, EquipableItem::setSpriteSheet);
		bind(alterationListEditor, EquipableItem::getStatModifiers, EquipableItem::setStatModifiers);

		// Layouting

		alterationListEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.statModifiers"));
		leftPanel = LayoutUtils.createVerticalBox(LayoutUtils.DEFAULT_GAP, 1, LayoutUtils.labelled("elementType.spriteSheet", spriteSheetChooser), alterationListEditor);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

	protected void finalizeLayouting() {
		LayoutUtils.addSideBySide(this, leftPanel, rightPanel);
	}

}
