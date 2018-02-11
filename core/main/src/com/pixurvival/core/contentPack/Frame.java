package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Frame {
	@XmlAttribute(required = true)
	private int x;
	@XmlAttribute(required = true)
	private int y;
}
