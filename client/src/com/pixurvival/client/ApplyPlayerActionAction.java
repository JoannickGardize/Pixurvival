package com.pixurvival.client;

import com.pixurvival.core.Action;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ApplyPlayerActionAction implements Action {

	private @NonNull PlayerEntity e;
	private @NonNull PlayerActionRequest request;

	@Override
	public void perform() {
		e.apply(request);
	}

}
