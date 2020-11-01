package com.pixurvival.core.contentPack.gameMode.role;

import com.pixurvival.core.contentPack.IdentifiedElement;

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

	private int minimumPerTeam;
	private int maximumPerTeam;
	private float recommandedRatioPerTeam;

	private StarterKit starterKit = new StarterKit();

	private WinCondition winCondition = new TeamSurvivedWinCondition();
}
