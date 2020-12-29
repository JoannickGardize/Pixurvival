package com.pixurvival.contentPackEditor.event;

import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceRenamedEvent extends Event {

	private String oldResourceName;
	private String newResourceName;
}
