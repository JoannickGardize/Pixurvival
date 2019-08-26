package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayedFollowingElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private long delay;

	private FollowingElement followingElement;
}
