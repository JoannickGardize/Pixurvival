package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayedFollowingElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Positive
	private long delay;

	@Valid
	private FollowingElement followingElement;
}
