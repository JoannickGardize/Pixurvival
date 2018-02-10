package com.pixurvival.core.contentPack;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "animationTemplates")
public class AnimationTemplates extends NamedElementSet<AnimationTemplate> {
	@Override
	@XmlElement(name = "animationTemplate")
	public List<AnimationTemplate> getListView() {
		return super.getListView();
	}
}
