package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EquipmentOffset extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@Length(min = 1)
	private List<FrameOffset> frameOffsets = new ArrayList<>();

}
