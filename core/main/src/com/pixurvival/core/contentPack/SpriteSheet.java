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
	@XmlElement(name = "image")
	@XmlJavaTypeAdapter(ImageReferenceAdapter.class)
	private ZipContentReference image;
	@XmlElement(name = "animationTemplate")
	@XmlJavaTypeAdapter(RefAdapter.AnimationTemplateRefAdapter.class)
	private AnimationTemplate animationTemplate;
}
