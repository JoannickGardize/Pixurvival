package com.pixurvival.core.contentPack.validation;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.reflection.visitor.VisitNode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorNode {

	private VisitNode node;

	private Object cause;

	@Override
	public String toString() {
		return pathString() + ": " + ErrorToString.toString(cause);
	}

	public String pathString() {
		Iterator<VisitNode> it = node.getAncestorHierarchy().iterator();
		StringBuilder sb = new StringBuilder();
		it.next();
		VisitNode firstNode = it.next();
		ElementList elementList = ((Field) firstNode.getKey()).getAnnotation(ElementList.class);
		if (elementList != null) {
			sb.append("(").append(elementList.value().getSimpleName()).append(") ").append(((IdentifiedElement) it.next().getObject()).getName());
		} else {
			sb.append(((Field) firstNode.getKey()).getName());
		}
		while (it.hasNext()) {
			sb.append(".").append(it.next().getKeyString());
		}
		return pathString();
	}

}
