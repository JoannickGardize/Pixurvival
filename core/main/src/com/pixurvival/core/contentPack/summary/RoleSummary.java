package com.pixurvival.core.contentPack.summary;

import com.pixurvival.core.contentPack.gameMode.role.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleSummary {

	private String name;

	private int minimumPerTeam;

	private int maximumPerTeam;

	public RoleSummary(Role role) {
		name = role.getName();
		minimumPerTeam = role.getMinimumPerTeam();
		maximumPerTeam = role.getMaximumPerTeam();
	}
}
