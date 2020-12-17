package com.pixurvival.contentPackEditor.component.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementInstanceChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;
import com.pixurvival.core.contentPack.IdentifiedElement;

public class LayoutTreeModel implements TreeModel {

	private List<TreeModelListener> listeners = new ArrayList<>();

	private LayoutFolder root = new LayoutFolder("loading...");
	private Map<IdentifiedElement, LayoutElement> elementsMap = new HashMap<>();

	public LayoutTreeModel() {
		EventManager.getInstance().register(this);
	}

	public void setRoot(LayoutFolder root) {
		this.root = root;
		setParentReferences(root);
		elementsMap.clear();
		root.forEachLeaf(node -> {
			LayoutElement layoutElement = (LayoutElement) node;
			elementsMap.put(layoutElement.getElement(), layoutElement);
		});
		root.forEachDeepFirst(LayoutNode::updateValidation);
		TreeModelEvent event = new TreeModelEvent(this, root.getPath());
		listeners.forEach(l -> l.treeStructureChanged(event));
	}

	@EventListener
	public void resourceListChanged(ResourceListChangedEvent event) {
		root.forEachDeepFirst(LayoutNode::updateValidation);
		TreeModelEvent modelEvent = new TreeModelEvent(this, root.getPath());
		listeners.forEach(l -> l.treeNodesChanged(modelEvent));
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		setRoot(LayoutManager.getInstance().getRoot());
	}

	@EventListener
	public void elementInstanceChanged(ElementInstanceChangedEvent event) {
		LayoutElement layoutElement = elementsMap.remove(event.getOldElement());
		layoutElement.setElement(event.getElement());

		// TODO Element linking
		root.forEachDeepFirst(LayoutNode::updateValidation);
		elementsMap.put(event.getElement(), layoutElement);
		TreeModelEvent modelEvent = new TreeModelEvent(this, root.getPath());
		listeners.forEach(l -> l.treeNodesChanged(modelEvent));
	}

	@EventListener
	public void elementChanged(ElementChangedEvent event) {
		elementsMap.get(event.getElement()).forEachAncestor(LayoutNode::updateValidation);
		TreeModelEvent modelEvent = new TreeModelEvent(this, root.getPath());
		listeners.forEach(l -> l.treeNodesChanged(modelEvent));
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
			if (node instanceof LayoutElement) {
				LayoutElement layoutElement = (LayoutElement) node;
				if (node.equals(elementsMap.get(layoutElement.getElement()))) {
					elementsMap.remove(layoutElement.getElement());
				}
			}
			root.forEachDeepFirst(LayoutNode::updateValidation);
			TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });
			listeners.forEach(l -> l.treeNodesRemoved(event));
			notifyNodeChanged(root);
		}
	}

	public void insert(LayoutNode node, LayoutNode parent, int index) {
		parent.getChildren().add(index, node);
		node.setParent(parent);
		if (node instanceof LayoutElement) {
			LayoutElement layoutElement = (LayoutElement) node;
			elementsMap.put(layoutElement.getElement(), layoutElement);
		}
		node.forEachAncestor(LayoutNode::updateValidation);
		TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });
		listeners.forEach(l -> l.treeNodesInserted(event));
	}

	public void notifyNodeChanged(LayoutNode node) {
		TreeModelEvent modelEvent = new TreeModelEvent(this, node.getPath());
		listeners.forEach(l -> l.treeNodesChanged(modelEvent));
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
}
