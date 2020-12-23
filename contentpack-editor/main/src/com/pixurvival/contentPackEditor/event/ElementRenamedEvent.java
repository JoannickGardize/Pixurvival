package com.pixurvival.contentPackEditor.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

@Getter
@AllArgsConstructor
public class ElementRenamedEvent extends Event {

	private String oldName;
	private NamedIdentifiedElement element;
}
