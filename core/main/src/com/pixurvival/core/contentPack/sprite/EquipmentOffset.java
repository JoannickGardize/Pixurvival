package com.pixurvival.core.contentPack.sprite;

import javax.xml.bind.annotation.XmlElement;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

@Getter
public class EquipmentOffset extends NamedElement {

	@XmlElement(name = "frameOffset")
	private FrameOffset[] frameOffsets;
}
