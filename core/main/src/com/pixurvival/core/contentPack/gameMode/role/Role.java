package com.pixurvival.core.contentPack.gameMode.role;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	public enum Visibility {
		NONE,
		SAME_ONLY,
		ALL
	}

	private Visibility teammatesVisiblity = Visibility.NONE;
	private Visibility enemiesVisiblity = Visibility.NONE;

	@Positive
	private int minimumPerTeam;

	@Positive
	private int maximumPerTeam;

	@Bounds(min = 0, max = 1, maxInclusive = false)
	private float recommandedRatioPerTeam;

	@Valid
	private StarterKit starterKit = new StarterKit();

	@Valid
	private WinCondition winCondition = new TeamSurvivedWinCondition();
}
