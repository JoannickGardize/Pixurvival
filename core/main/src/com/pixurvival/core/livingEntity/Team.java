package com.pixurvival.core.livingEntity;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "id")
public class Team {

	private short id;
	private String name;

	private List<PlayerEntity> members = new ArrayList<>();

	public Team(short id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
