package com.pixurvival.contentPackEditor.component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@Deprecated
public class RootElementTree extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTree tree = new JTree();

	public RootElementTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("test");
		root.add(new DefaultMutableTreeNode("coucou"));

		tree = new JTree(root);
		tree.setEditable(true);
		tree.setShowsRootHandles(true);

		add(tree);
		root.add(new DefaultMutableTreeNode(new Object()));
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(root);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setContentPane(new RootElementTree());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
