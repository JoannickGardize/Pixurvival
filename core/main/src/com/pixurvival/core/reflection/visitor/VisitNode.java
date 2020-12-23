package com.pixurvival.core.reflection.visitor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

	public Collection<VisitNode> children() {
		return children.values();
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
		StringBuilder sb = new StringBuilder();
		Iterator<VisitNode> it = getAncestorHierarchy().iterator();
		it.next();
		if (!it.hasNext()) {
			return "";
		}
		sb.append(it.next().getKeyString());
		while (it.hasNext()) {
			sb.append('.');
			sb.append(it.next().getKeyString());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return pathString();
	}

	/**
	 * Returns a list of all ancestors of this node, starting from the root node and
	 * finishing to this node. The list has at least one element, this instance, if
	 * this is the root node.
	 * 
	 * @return a list containing all ancestors starting from the root node
	 */
	public List<VisitNode> getAncestorHierarchy() {
		List<VisitNode> nodes = new ArrayList<>();
		VisitNode currentNode = this;
		while (currentNode != null) {
			nodes.add(currentNode);
			currentNode = currentNode.getParent();
		}
		Collections.reverse(nodes);
		return nodes;
	}

	public String getKeyString() {
		if (key instanceof Field) {
			return ((Field) key).getName();
		} else {
			return key.toString();
		}
	}

}
