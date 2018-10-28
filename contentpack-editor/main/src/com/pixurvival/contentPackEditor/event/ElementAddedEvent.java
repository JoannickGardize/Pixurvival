package com.pixurvival.contentPackEditor.event;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementAddedEvent extends Event {

	private NamedElement element;
}
