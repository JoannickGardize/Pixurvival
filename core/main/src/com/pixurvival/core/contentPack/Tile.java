package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter

public class Tile extends NamedElement {

	public static final byte SPECIAL_TILE = -1;

	private @Setter(AccessLevel.PACKAGE) byte id;
	@XmlAttribute(name = "solid")
	private boolean solid = false;
	@XmlElement(name = "velocityFactor")
	@XmlJavaTypeAdapter(VelocityFactorAdapter.class)
	private Float velocityFactor = 1f;
	@XmlElement(name = "frame")
	private Frame[] frames;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VelocityFactorWrapper {
		@XmlValue
		float value;
	}

	public static class VelocityFactorAdapter extends XmlAdapter<VelocityFactorWrapper, Float> {

		@Override
		public Float unmarshal(VelocityFactorWrapper v) throws Exception {
			return v.getValue();
		}

		@Override
		public VelocityFactorWrapper marshal(Float v) throws Exception {
			return new VelocityFactorWrapper(v);
		}
	}
}
