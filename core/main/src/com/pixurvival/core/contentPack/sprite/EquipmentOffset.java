package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EquipmentOffset extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private FrameOffset[] frameOffsets;
}
