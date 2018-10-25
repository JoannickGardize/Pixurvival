package com.pixurvival.core.contentPack;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimensions implements Serializable {

	private static final long serialVersionUID = 1L;

	private int width;

	private int height;
}
