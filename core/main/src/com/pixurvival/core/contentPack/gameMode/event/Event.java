package com.pixurvival.core.contentPack.gameMode.event;

import java.io.Serializable;

import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	private long startTime;
	private long repeatTime;

	public abstract void perform(World world, int repeatCount);
}
