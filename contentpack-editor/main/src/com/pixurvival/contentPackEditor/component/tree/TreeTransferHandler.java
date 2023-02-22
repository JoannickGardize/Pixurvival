package com.pixurvival.contentPackEditor.component.tree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

public class TreeTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;

    private DataFlavor nodesFlavor;
    private DataFlavor[] flavors = new DataFlavor[1];
    private LayoutNode[] nodesToRemove;

    public TreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + LayoutNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        // Do not allow a drop on the drag source selections.
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        JTree tree = (JTree) support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        for (int i = 0; i < selRows.length; i++) {
            if (selRows[i] == dropRow) {
                return false;
            }
        }
        // Do not allow MOVE-action drops if a non-leaf node is
        // selected unless all of its children are also selected.
        int action = support.getDropAction();
        if (action == MOVE) {
            return haveCompleteNode(tree);
        }
        // Do not allow a non-leaf node to be copied to a level
        // which is less than its source level.
        TreePath dest = dl.getPath();
        TreePath path = tree.getPathForRow(selRows[0]);
        LayoutNode firstNode = (LayoutNode) path.getLastPathComponent();
        return !firstNode.getChildren().isEmpty() && dest.getPathCount() < path.getPathCount();
    }

    private boolean haveCompleteNode(JTree tree) {
        int[] selRows = tree.getSelectionRows();
        TreePath path = tree.getPathForRow(selRows[0]);
        LayoutNode first = (LayoutNode) path.getLastPathComponent();
        int childCount = first.getChildren().size();
        // first has children and no children are selected.
        if (childCount > 0 && selRows.length == 1) {
            return false;
        }
        // first may have children.
        for (int i = 1; i < selRows.length; i++) {
            path = tree.getPathForRow(selRows[i]);
            LayoutNode next = (LayoutNode) path.getLastPathComponent();
            if (first.getChildren().contains(next)) {
                // Found a child of first.
                if (childCount > selRows.length - 1) {
                    // Not all children of first are selected.
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            // Make up a node array of copies for transfer and
            // another for/of the nodes that will be removed in
            // exportDone after a successful drop.
            List<LayoutNode> copies = new ArrayList<>();
            List<LayoutNode> toRemove = new ArrayList<>();
            LayoutNode node = (LayoutNode) paths[0].getLastPathComponent();
            LayoutNode copy = node.copy();
            copies.add(copy);
            toRemove.add(node);
            for (int i = 1; i < paths.length; i++) {
                LayoutNode next = (LayoutNode) paths[i].getLastPathComponent();
                // Do not allow higher level nodes to be added to list.
                if (paths[i].getPathCount() < paths[0].getPathCount()) {
                    break;
                } else if (paths[i].getPathCount() > paths[0].getPathCount()) { // child
                    // node
                    copy.getChildren().add(next.copy());
                    // node already contains child
                } else { // sibling
                    copies.add(next.copy());
                    toRemove.add(next);
                }
            }
            LayoutNode[] nodes = copies.toArray(new LayoutNode[copies.size()]);
            nodesToRemove = toRemove.toArray(new LayoutNode[toRemove.size()]);
            return new NodesTransferable(nodes);
        }
        return null;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            LayoutTreeModel model = (LayoutTreeModel) tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.remove(nodesToRemove[i]);
            }
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        // Extract transfer data.
        LayoutNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (LayoutNode[]) t.getTransferData(nodesFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            return false;
        } catch (java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
            return false;
        }
        // Get drop location info.
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        LayoutNode parent = (LayoutNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        LayoutTreeModel model = (LayoutTreeModel) tree.getModel();
        // Configure for drop mode.
        int index = childIndex; // DropMode.INSERT
        if (childIndex == -1) { // DropMode.ON
            index = parent.getChildren().size();
        }
        // Add data to model.
        for (int i = 0; i < nodes.length; i++) {
            model.insert(nodes[i], parent, index++);
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

    public class NodesTransferable implements Transferable {
        LayoutNode[] nodes;

        public NodesTransferable(LayoutNode[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
