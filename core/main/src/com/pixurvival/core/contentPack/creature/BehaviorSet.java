package com.pixurvival.core.contentPack.creature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BehaviorSet extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@ElementCollection(value = Behavior.class, isRoot = false)
	private List<Behavior> behaviors = new ArrayList<>();
}
