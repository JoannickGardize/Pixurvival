package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

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

	@Bounds(min = 0)
	private double frameDuration = 1;

	@NonNull
	@Required
	private ActionAnimation action;

	@Valid
	private List<Frame> frames = new ArrayList<>();

	@Override
	public String toString() {
		return action.name();
	}
}
