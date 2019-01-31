package com.pixurvival.core.reflection.visitor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class VisitNode {

	private final VisitNode root;

	private final VisitNode parent;

	private final @Getter(AccessLevel.NONE) Map<Object, VisitNode> children = new HashMap<>();

	private Object userData;

	private final Object key;

	private final Object object;

	private VisitNode(VisitNode parent, Object key, Object object) {
		super();
		this.root = parent.root;
		this.parent = parent;
		this.key = key;
		this.object = object;
	}

	public VisitNode(Object object) {
		this.object = object;
		root = this;
		parent = null;
		key = null;
	}

	public VisitNode addChild(Object key, Object child) {
		VisitNode childNode = new VisitNode(this, key, child);
		children.put(key, childNode);
		return childNode;
	}

	public VisitNode getChild(Object key) {
		return children.get(key);
	}

	public VisitNode getAncestor(int depth) {
		VisitNode tmp = this;
		for (int i = 0; i < depth; i++) {
			tmp = tmp.getParent();
		}
		return tmp;
	}

	public VisitNode findChild(Predicate<VisitNode> condition) {
		for (VisitNode child : children.values()) {
			if (condition.test(child)) {
				return child;
			}
		}
		return null;
	}

	public String pathString() {
		List<String> pathElements = new ArrayList<>();
		VisitNode currentNode = this;
		while (currentNode.getKey() != null) {
			pathElements.add(currentNode.getKeyString());
			currentNode = currentNode.getParent();
		}
		StringBuilder sb = new StringBuilder();
		int start = pathElements.size() - 1;
		for (int i = start; i >= 0; i--) {
			if (i != start) {
				sb.append('.');
			}
			sb.append(pathElements.get(i));
		}
		return sb.toString();
	}

	private String getKeyString() {
		if (key instanceof Field) {
			return ((Field) key).getName();
		} else {
			return key.toString();
		}
	}

}
