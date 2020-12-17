package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Frame implements Serializable {

	private static final long serialVersionUID = 1L;

	@Positive
	private int x;

	@Positive
	private int y;

}
