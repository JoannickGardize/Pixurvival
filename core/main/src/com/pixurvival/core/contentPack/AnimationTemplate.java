package com.pixurvival.core.contentPack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.pixurvival.core.util.ListViewOfMap;

import lombok.Getter;

public class AnimationTemplate extends NamedElement {

	@Getter
	@XmlAttribute(name = "frameDuration")
	private double frameDuration;

	private Map<String, Animation> animations = new HashMap<>();

	public Map<String, Animation> getAnimations() {
		return Collections.unmodifiableMap(animations);
	}

	@XmlElement(name = "animation")
	public List<Animation> getAnimationsListView() {
		return new ListViewOfMap<String, Animation>(animations, Animation::getName);
	}
}