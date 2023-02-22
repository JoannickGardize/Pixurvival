package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.util.ReflectionUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A node path represents a path between objects through instances of
 * {@link VisitNode}.
 *
 * @author SharkHendrix
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NodePath {

    private static interface NodePathStep {
        VisitNode apply(VisitNode node) throws NodePathException;
    }

    private String pathString;
    private NodePathStep[] blueprint;

    /**
     * Apply this path to the given starting node, and returns the node according to
     * this path.
     *
     * @param startNode the node to start
     * @return the node relative to the {@code startingNode} according to this path
     * @throws NodePathException if a node has not been found
     */
    public VisitNode apply(VisitNode startNode) throws NodePathException {
        VisitNode currentNode = startNode;
        VisitNode nextNode;
        for (int i = 0; i < blueprint.length; i++) {
            nextNode = blueprint[i].apply(currentNode);
            if (currentNode == null) {
                throw new NodePathException("Next node of node " + currentNode + " not found for path step " + (i + 1) + " of node path " + this);
            }
            currentNode = nextNode;
        }
        return currentNode;
    }

    /**
     * <p>
     * Creates a node from its string representation.
     * <p>
     * A node path starts with zero, one or many {@code "<"} characters, indicating
     * the number of parent to go up in the visiting tree.<br>
     * Then, one or many field names are specified, starting with a {@code "."}, to
     * go down in the visiting tree.
     * <p>
     * The {@link #toString()} representation of the newly created instance is
     * exactly the given path string argument.
     *
     * @param pathString the path's string representation
     * @return an instance of NodePath according to the given string representation
     * @throws IllegalArgumentException if the path string is malformed
     */
    public static NodePath of(String pathString) {
        int currentIndex = 0;
        List<NodePathStep> steps = new ArrayList<>();
        while (currentIndex < pathString.length()) {
            char currentChar = pathString.charAt(currentIndex);
            switch (currentChar) {
                case '<':
                    currentIndex = addParentStep(currentIndex, steps);
                    break;
                case '.':
                    currentIndex = addFieldStep(currentIndex, pathString, steps);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected character '" + currentChar + "' at index " + currentIndex + " for node path \"" + pathString + "\"");
            }
        }
        return new NodePath(pathString, steps.stream().toArray(NodePathStep[]::new));
    }

    @Override
    public String toString() {
        return pathString;
    }

    private static int addParentStep(int currentIndex, List<NodePathStep> steps) {
        steps.add(VisitNode::getParent);
        return currentIndex + 1;
    }

    private static int addFieldStep(int currentIndex, String pathString, List<NodePathStep> steps) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int newCurrentIndex = currentIndex + 1;
        for (; newCurrentIndex < pathString.length(); newCurrentIndex++) {
            char c = pathString.charAt(newCurrentIndex);
            if (c == '.') {
                break;
            } else {
                fieldNameBuilder.append(c);
            }
        }
        String fieldName = fieldNameBuilder.toString();
        steps.add(n -> {
            try {
                return n.getChild(ReflectionUtils.getField(n.getObject().getClass(), fieldName));
            } catch (NoSuchFieldException e) {
                throw new NodePathException(e);
            }
        });
        return newCurrentIndex;
    }
}
