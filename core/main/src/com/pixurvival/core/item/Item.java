package com.pixurvival.core.item;

import java.io.Serializable;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item extends IdentifiedElement implements Serializable {

	@Getter
	@Setter
	public static class StructureDetails {

		private static final long serialVersionUID = 1L;

		@Required
		@ElementReference
		private Structure structure;

	}

	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private int maxStackSize;

	@Valid
	private Frame frame;

	@Required
	@ResourceReference
	private String image;

	public Item(String name, int index) {
		super(name, index);
	}

}
