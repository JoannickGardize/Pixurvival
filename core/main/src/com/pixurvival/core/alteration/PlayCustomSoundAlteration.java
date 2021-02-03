package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.ResourceType;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayCustomSoundAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@ResourceReference(type = ResourceType.SOUND)
	private String sound;

	private transient int soundId;

	@Override
	public void uniqueApply(TeamMember source, TeamMember entity) {
		PlaySoundAlteration.playSound(entity, soundId);
	}

}
