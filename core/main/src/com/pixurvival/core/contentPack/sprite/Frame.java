package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Frame implements Serializable {

	private static final long serialVersionUID = 1L;

	private int x;
	private int y;

}
