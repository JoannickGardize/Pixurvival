package com.pixurvival.contentPackEditor.component.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class LayoutNode {

    @Getter
    @Setter
    private transient LayoutNode parent;

    @Getter
    @Setter
    private transient boolean valid = true;

    public abstract boolean isLeaf();

    public abstract LayoutNode copy();

    public abstract void updateValidation();

    public List<LayoutNode> getChildren() {
        return Collections.emptyList();
    }

    public void forEachLeaf(Consumer<LayoutNode> action) {
        getChildren().forEach(node -> {
            if (node.isLeaf()) {
                action.accept(node);
            }
            node.forEachLeaf(action);
        });
    }

    public void forEachFolderDeepFirst(Consumer<LayoutNode> action) {
        getChildren().forEach(node -> {
            if (!node.isLeaf()) {
                node.forEachLeaf(action);
            }
            action.accept(node);
        });
    }

    public void forEachDeepFirst(Consumer<LayoutNode> action) {
        getChildren().forEach(node -> node.forEachDeepFirst(action));
        action.accept(this);
    }

    public void forEachAncestor(Consumer<LayoutNode> action) {
        action.accept(this);
        if (parent != null) {
            parent.forEachAncestor(action);
        }
    }

    public Object[] getPath() {
        List<Object> constructionList = new ArrayList<>();
        appendForPath(constructionList);
        return constructionList.toArray();
    }

    public void appendForPath(List<Object> constructionList) {
        if (parent != null) {
            parent.appendForPath(constructionList);
        }
        constructionList.add(this);
    }

}
