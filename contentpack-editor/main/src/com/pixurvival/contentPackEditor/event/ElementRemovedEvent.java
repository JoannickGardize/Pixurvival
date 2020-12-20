package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementRemovedEvent extends Event {

	private IdentifiedElement element;
}
