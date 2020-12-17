package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorMapping<T extends IdentifiedElement> {

	@ElementReference
	private T element;

	@Bounds(min = 0, max = 0xffffff, maxInclusive = true)
	private int color;
}
