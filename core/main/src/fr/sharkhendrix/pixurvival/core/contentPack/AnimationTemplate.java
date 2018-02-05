package fr.sharkhendrix.pixurvival.core.contentPack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import fr.sharkhendrix.pixurvival.core.util.ListViewOfMap;
import lombok.Getter;

public class AnimationTemplate {

	@Getter
	@XmlElement
	private String name;

	@Getter
	@XmlElement
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
