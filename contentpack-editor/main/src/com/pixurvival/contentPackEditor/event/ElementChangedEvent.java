package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementChangedEvent extends Event {

	private NamedIdentifiedElement element;
	private boolean valid;
}
