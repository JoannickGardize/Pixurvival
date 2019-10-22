package com.pixurvival.contentPackEditor.component.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.pixurvival.core.contentPack.IdentifiedElement;

public class LayoutTreeModel implements TreeModel {

	private List<TreeModelListener> listeners = new ArrayList<>();

	private LayoutFolder root = new LayoutFolder("loading...");
	private Map<IdentifiedElement, LayoutElement> elementsMap = new HashMap<>();

	public void setRoot(LayoutFolder root) {
		this.root = root;
		setParentReferences(root);
		elementsMap.clear();
		updateAllValidation();
		TreeModelEvent event = new TreeModelEvent(this, root.getPath());
		listeners.forEach(l -> l.treeStructureChanged(event));
	}

	private void setParentReferences(LayoutNode node) {
		for (LayoutNode child : node.getChildren()) {
			child.setParent(node);
			setParentReferences(child);
		}
	}

	public void remove(LayoutNode node) {
		LayoutNode parent = node.getParent();
		if (parent != null) {
			int index = parent.getChildren().indexOf(node);
			parent.getChildren().remove(index);
			parent.forEachAncestor(LayoutNode::updateValidation);
			TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });
			listeners.forEach(l -> l.treeNodesRemoved(event));
		}
	}

	public void insert(LayoutNode node, LayoutNode parent, int index) {
		parent.getChildren().add(index, node);
		node.setParent(parent);
		parent.forEachAncestor(LayoutNode::updateValidation);
		TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });
		listeners.forEach(l -> l.treeNodesInserted(event));
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((LayoutNode) parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((LayoutNode) parent).getChildren().size();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((LayoutNode) node).isLeaf();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Nothing
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((LayoutNode) parent).getChildren().indexOf(child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	private void updateAllValidation() {
		root.forEachLeaf(node -> {
			LayoutElement layoutElement = (LayoutElement) node;
			elementsMap.put(layoutElement.getElement(), layoutElement);
			layoutElement.updateValidation();
		});
		root.forEachFolderDeepFirst(LayoutNode::updateValidation);
	}
}
