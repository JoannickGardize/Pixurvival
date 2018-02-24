package com.pixurvival.core.message;

import com.pixurvival.core.util.ByteArray2D;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MapPart {

	private int x;
	private int y;
	private ByteArray2D data;
}
