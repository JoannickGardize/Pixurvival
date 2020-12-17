package com.pixurvival.core.contentPack.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pixurvival.core.reflection.visitor.VisitNode;

public class ErrorCollection {

	private List<ErrorNode> errorNodes = new ArrayList<>();

	public void add(VisitNode node, Object cause) {
		errorNodes.add(new ErrorNode(node, cause));
	}

	public List<ErrorNode> asList() {
		return Collections.unmodifiableList(errorNodes);
	}
}
