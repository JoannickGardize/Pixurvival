package com.pixurvival.core.message;

import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StartGame {

	public long worldTime = 0;
	public Vector2 spawnCenter;
}
