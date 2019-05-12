package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoubleDimensions implements Serializable {
	private static final long serialVersionUID = 1L;

	@Bounds(min = 1)
	private double width;

	@Bounds(min = 1)
	private double height;
}
