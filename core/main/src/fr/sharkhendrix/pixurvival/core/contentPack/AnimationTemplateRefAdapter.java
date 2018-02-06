package fr.sharkhendrix.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class AnimationTemplateRefAdapter extends XmlAdapter<String, AnimationTemplate> {

	private @NonNull AnimationTemplates animationTemplates;

	@Override
	public AnimationTemplate unmarshal(String v) throws Exception {
		AnimationTemplate template = animationTemplates.getAnimationTemplates().get(v);
		if (template == null) {
			throw new ContentPackReadException("No animationTemplate found with name : " + v);
		}
		return template;
	}

	@Override
	public String marshal(AnimationTemplate v) throws Exception {
		return v.getName();
	}

}
