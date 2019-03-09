package com.pixurvival.core.contentPack.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtificialIntelligence implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Behavior> behaviors = new ArrayList<>();
}
