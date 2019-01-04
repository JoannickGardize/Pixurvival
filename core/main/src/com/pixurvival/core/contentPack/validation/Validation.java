package com.pixurvival.core.contentPack.validation;

import java.util.List;

public class InvalidMessages {

	private List<String> path;
	private List<String> messages;

	public void forward(String item) {
		path.add(item);
	}

	public void backward() {
		if (!path.isEmpty()) {
			path.remove(path.size() - 1);
		}
	}
}
