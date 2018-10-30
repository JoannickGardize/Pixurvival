package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementChangedEvent extends Event {

	private NamedElement element;
	private boolean valid;
}
