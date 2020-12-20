package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Animation implements Serializable {

	public static final float MIN_FRAME_DURATION = 30;

	private static final long serialVersionUID = 1L;

	@Bounds(min = Animation.MIN_FRAME_DURATION)
	private long frameDuration = 1000;

	@NonNull
	private ActionAnimation action;

	@Valid
	@Length(min = 1)
	private List<Frame> frames = new ArrayList<>();

	private float rotationPerSecond = 0;

	@Override
	public String toString() {
		return action.name();
	}
}
