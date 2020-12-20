package com.pixurvival.contentPackEditor.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.pixurvival.core.contentPack.IdentifiedElement;

@Getter
@AllArgsConstructor
public class ElementRenamedEvent extends Event {

	private String oldName;
	private IdentifiedElement element;
}
