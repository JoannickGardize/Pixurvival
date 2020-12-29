package com.pixurvival.core.contentPack.validation;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
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
			if (it.hasNext()) {
				sb.append("(").append(elementList.value().getSimpleName()).append(") ").append(((NamedIdentifiedElement) it.next().getObject()).getName());
			} else {
				return "List of " + elementList.value().getSimpleName();
			}
		} else {
			sb.append(((Field) firstNode.getKey()).getName());
		}
		while (it.hasNext()) {
			VisitNode currentNode = it.next();
			if (currentNode.getKey() instanceof Field) {
				sb.append(".").append(((Field) currentNode.getKey()).getName());
			} else if (currentNode.getKey() instanceof Integer) {
				sb.append("[").append((int) currentNode.getKey() + 1).append("]");
			} else {
				sb.append("[").append(currentNode.getKey()).append("]");
			}
		}
		return sb.toString();
	}

}
