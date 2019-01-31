package com.pixurvival.core.contentPack.validation;

import com.pixurvival.core.reflection.visitor.VisitNode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidNode {

	private VisitNode node;

	private Object cause;

	@Override
	public String toString() {
		return node.pathString() + " : " + cause;
	}
}
