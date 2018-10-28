package com.pixurvival.contentPackEditor.event;

import com.pixurvival.contentPackEditor.component.ElementType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementTypeChooseEvent extends Event {

	private ElementType elementType;
}
