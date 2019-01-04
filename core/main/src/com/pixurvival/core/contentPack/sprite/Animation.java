package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Animation implements Serializable {

	public static final double MIN_FRAME_DURATION = 0.01;

	private static final long serialVersionUID = 1L;

	private double frameDuration = 1;

	@NonNull
	private ActionAnimation action;

	private List<Frame> frames = new ArrayList<>();

	@Override
	public String toString() {
		return action.name();
	}
}
