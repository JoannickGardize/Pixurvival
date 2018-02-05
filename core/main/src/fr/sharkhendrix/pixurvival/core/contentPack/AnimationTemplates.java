package fr.sharkhendrix.pixurvival.core.contentPack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.sharkhendrix.pixurvival.core.util.ListViewOfMap;

@XmlRootElement(name = "animationTemplates")
public class AnimationTemplates {

	private Map<String, AnimationTemplate> animationTemplates = new HashMap<>();

	public Map<String, AnimationTemplate> getAnimationTemplates() {
		return Collections.unmodifiableMap(animationTemplates);
	}

	@XmlElement(name = "animationTemplate")
	public List<AnimationTemplate> getAnimationsListView() {
		return new ListViewOfMap<String, AnimationTemplate>(animationTemplates, AnimationTemplate::getName);
	}
}
