package com.pixurvival.contentPackEditor.component.tree;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LayoutElement extends LayoutNode {

	private transient IdentifiedElement element;

	private ElementType type;
	private int id;

	public LayoutElement(IdentifiedElement element) {
		this.element = element;
		type = ElementType.of(element);
		id = element.getId();
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public LayoutNode copy() {
		LayoutElement layoutElement = new LayoutElement(element, type, id);
		layoutElement.setValid(isValid());
		return layoutElement;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateValidation() {
		setValid(ContentPackEditionService.getInstance().editorOf(ElementType.of(element)).isValueValid(element));
	}

}
