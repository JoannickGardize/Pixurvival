package com.pixurvival.core.contentPack.gameMode.event;

import java.io.Serializable;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	@Positive
	private long startTime;
	@Positive
	private long repeatTime;

	public abstract void perform(World world, int repeatCount);
}
