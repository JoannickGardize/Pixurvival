package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Animation implements Serializable {

	private static final long serialVersionUID = 1L;

	private double frameDuration = 1;

	private ActionAnimation action;

	private List<Frame> frames;
}
