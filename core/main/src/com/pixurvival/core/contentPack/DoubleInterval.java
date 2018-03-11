package com.pixurvival.core.contentPack;

import java.util.Random;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class DoubleInterval {

	@XmlAttribute(name = "min")
	private double min;
	@XmlAttribute(name = "max")
	private double max;

	public double next(Random random) {
		return random.nextDouble() * (max - min) + min;
	}
}
