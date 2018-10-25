package com.pixurvival.core.contentPack.sprite;

import java.io.Serializable;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentOffset extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private FrameOffset[] frameOffsets;
}
