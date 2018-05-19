package com.pixurvival.core.contentPack.sprite;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class FrameOffset {
	@XmlAttribute(required = true)
	private int x;
	@XmlAttribute(required = true)
	private int y;
	@XmlAttribute(required = true)
	private int offsetX;
	@XmlAttribute(required = true)
	private int offsetY;
	@XmlAttribute
	private boolean back;
}
