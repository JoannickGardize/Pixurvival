package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorMapping<T extends NamedIdentifiedElement> {

	@ElementReference
	private T element;

	private int color;
}
