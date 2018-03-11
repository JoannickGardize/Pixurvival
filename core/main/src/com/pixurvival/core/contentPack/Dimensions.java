package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Dimensions {

	@XmlAttribute(name = "width")
	private int width;

	@XmlAttribute(name = "height")
	private int height;
}
