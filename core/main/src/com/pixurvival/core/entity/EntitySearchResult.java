package com.pixurvival.core.entity;

import lombok.Data;

@Data
public class EntitySearchResult {

	private Entity entity = null;
	private float distanceSquared = Float.POSITIVE_INFINITY;
}
