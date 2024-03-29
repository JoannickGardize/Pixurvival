package com.pixurvival.contentPackEditor.component.tree;

import com.pixurvival.contentPackEditor.event.*;
import com.pixurvival.contentPackEditor.relationGraph.ElementRelationService;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayoutTreeModel implements TreeModel {

    private List<TreeModelListener> listeners = new ArrayList<>();

    private LayoutFolder root = new LayoutFolder("loading...");
    private Map<NamedIdentifiedElement, LayoutElement> elementsMap = new HashMap<>();

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
    public void resourceAdded(ResourceAddedEvent event) {
        updateResourceReferentElementsValidation(event.getResourceName());
        notifyNodeChanged(root);
    }

    @EventListener
    public void resourceChanged(ResourceChangedEvent event) {
        updateResourceReferentElementsValidation(event.getResourceName());
        notifyNodeChanged(root);
    }

    @EventListener
    public void resourceRemoved(ResourceRemovedEvent event) {
        updateResourceReferentElementsValidation(event.getResourceName());
        notifyNodeChanged(root);
    }

    @EventListener
    public void resourceRenamed(ResourceRenamedEvent event) {
        updateResourceReferentElementsValidation(event.getOldResourceName());
        updateResourceReferentElementsValidation(event.getNewResourceName());
        notifyNodeChanged(root);
    }

    @EventListener
    public void contentPackLoaded(ContentPackLoadedEvent event) {
        setRoot(LayoutManager.getInstance().getRoot());
    }

    @EventListener
    public void elementInstanceChanged(ElementInstanceChangedEvent event) {
        LayoutElement layoutElement = elementsMap.remove(event.getOldElement());
        layoutElement.setElement(event.getElement());
        updateReferentElementsValidation(event.getOldElement());
        updateReferentElementsValidation(event.getElement());
        elementsMap.put(event.getElement(), layoutElement);
        notifyNodeChanged(root);
    }

    @EventListener
    public void elementChanged(ElementChangedEvent event) {
        elementsMap.get(event.getElement()).forEachAncestor(LayoutNode::updateValidation);
        // TODO manage specific updates in a more generic way
        if (event.getElement() instanceof AnimationTemplate) {
            ElementRelationService.getInstance().forEachReferent(event.getElement(), this::updateReferentElementsValidation);
            updateReferentElementsValidation(event.getElement());
        } else if (event.getElement() instanceof SpriteSheet || event.getElement() instanceof EquipmentOffset || event.getElement() instanceof BehaviorSet
                || event.getElement() instanceof AbilitySet) {
            updateReferentElementsValidation(event.getElement());
        }
        notifyNodeChanged(root);
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
                removeElement((LayoutElement) node);
            } else {
                node.forEachDeepFirst(n -> {
                    if (n instanceof LayoutElement) {
                        removeElement((LayoutElement) n);
                    }
                });
            }
            TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[]{index}, new Object[]{node});
            listeners.forEach(l -> l.treeNodesRemoved(event));
            notifyNodeChanged(root);
        }
    }

    private void removeElement(LayoutElement layoutElement) {
        if (layoutElement.equals(elementsMap.get(layoutElement.getElement()))) {
            elementsMap.remove(layoutElement.getElement());
        }
        updateReferentElementsValidation(layoutElement.getElement());
    }

    public void insert(LayoutNode node, LayoutNode parent, int index) {
        parent.getChildren().add(index, node);
        node.setParent(parent);
        if (node instanceof LayoutElement) {
            LayoutElement layoutElement = (LayoutElement) node;
            elementsMap.put(layoutElement.getElement(), layoutElement);
        }
        node.forEachAncestor(LayoutNode::updateValidation);
        TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[]{index}, new Object[]{node});
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

    private void updateReferentElementsValidation(NamedIdentifiedElement referenced) {
        ElementRelationService.getInstance().forEachReferent(referenced, this::refreshElement);
    }

    private void updateResourceReferentElementsValidation(String referenced) {
        ElementRelationService.getInstance().forEachResourceReferent(referenced, this::refreshElement);
    }

    private void refreshElement(NamedIdentifiedElement e) {
        LayoutElement layoutElement = elementsMap.get(e);
        if (layoutElement != null) {
            layoutElement.forEachAncestor(LayoutNode::updateValidation);
        }
    }
}
