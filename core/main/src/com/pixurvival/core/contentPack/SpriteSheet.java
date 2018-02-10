package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;

@Getter
public class SpriteSheet extends NamedElement {

	@XmlAttribute(name = "width")
	private int width;
	@XmlAttribute(name = "height")
	private int height;
	@XmlElement(name = "fileName")
	private String fileName;
	@XmlElement(name = "animationTemplate")
	@XmlJavaTypeAdapter(NamedElementRefAdapter.AnimationTemplateRefAdapter.class)
	private AnimationTemplate animationTemplate;
}
