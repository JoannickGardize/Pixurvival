package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.Item.Equipable;
import com.pixurvival.core.livingEntity.alteration.PersistentStatAlteration;

public abstract class EquipableEditor<T extends Equipable> extends ElementEditor<T> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<SpriteSheet> spriteSheetChooser = new ElementChooserButton<>(LayoutUtils.getSpriteSheetIconProvider());

	public EquipableEditor() {
		EventManager.getInstance().register(this);

		// Construction

		ListEditor<PersistentStatAlteration> alterationListEditor = new VerticalListEditor<>(PersistentStatAlterationEditor::new, PersistentStatAlteration::new);

		// Binding

		bind(spriteSheetChooser, Equipable::getSpriteSheet, Equipable::setSpriteSheet);
		bind(alterationListEditor, Equipable::getAlterations, Equipable::setAlterations);

		// Layouting

		alterationListEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.statAlterations"));
		LayoutUtils.addVertically(this, LayoutUtils.NORMAL_GAP, 1, LayoutUtils.labelled("elementType.spriteSheet", spriteSheetChooser), alterationListEditor);
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		spriteSheetChooser.setItems(event.getContentPack().getSpriteSheets());
	}

}
