package com.pixurvival.contentPackEditor.component.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.Utils;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.util.MenuBuilder;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.util.CaseUtils;

public class LayoutTree extends JTree {

	private static final long serialVersionUID = 1L;

	private int currentContextX;
	private int currentContextY;

	public LayoutTree() {
		JPopupMenu contextMenu = new JPopupMenu();
		MenuBuilder menuBuilder = new MenuBuilder(contextMenu, "treeContextMenu");
		menuBuilder.addItem("new.folder", this::newFolder);
		for (ElementType type : ElementType.values()) {
			menuBuilder.addItem("new." + CaseUtils.upperToCamelCase(type.name()), () -> this.newElement(type), type.toString());
		}

		setModel(new LayoutTreeModel());
		setCellRenderer(new LayoutTreeCellRenderer());
		setRootVisible(false);
		setShowsRootHandles(true);
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		setTransferHandler(new TreeTransferHandler());
		getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		EventManager.getInstance().register(this);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					currentContextX = e.getX();
					currentContextY = e.getY();
					contextMenu.show(LayoutTree.this, e.getX(), e.getY());
				}
			}
		});

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (e.isAddedPath()) {
					TreePath path = e.getPath();
					// TODO
				}

			}
		});

	}

	private LayoutNode getRightClickedNode() {
		return (LayoutNode) getClosestPathForLocation(currentContextX, currentContextY).getLastPathComponent();

	}

	private void newFolder() {
		LayoutNode selectedNode = getRightClickedNode();
		String name = showChooseFolderNameDialog("");
		if (name == null) {
			return;
		}
		LayoutTreeModel model = ((LayoutTreeModel) getModel());
		if (selectedNode instanceof LayoutElement) {
			LayoutNode parent = selectedNode.getParent();
			model.insert(new LayoutFolder(name), parent, model.getIndexOfChild(parent, selectedNode) + 1);
		} else {
			model.insert(new LayoutFolder(name), selectedNode, selectedNode.getChildren().size());
		}
	}

	private void newElement(ElementType type) {
		String name = showChooseElementNameDialog(type, "");
		if (name == null) {
			return;
		}
		IdentifiedElement newElement = ContentPackEditionService.getInstance().addElement(type, name);
		if (newElement == null) {
			return;
		}
		LayoutNode selectedNode = getRightClickedNode();
		LayoutTreeModel model = ((LayoutTreeModel) getModel());
		LayoutElement element = new LayoutElement(newElement);
		element.updateValidation();
		if (selectedNode instanceof LayoutElement) {
			LayoutNode parent = selectedNode.getParent();
			model.insert(element, parent, model.getIndexOfChild(parent, selectedNode) + 1);
		} else {
			model.insert(element, selectedNode, selectedNode.getChildren().size());
		}
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		((LayoutTreeModel) getModel()).setRoot(LayoutManager.getInstance().getRoot());
	}

	private String showChooseFolderNameDialog(String defaultName) {
		String name = JOptionPane.showInputDialog(SwingUtilities.getRoot(this), TranslationService.getInstance().getString("treeContextMenu.new.folder.chooseNameMessage"), defaultName);
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (name.length() == 0) {
			Utils.showErrorDialog("treeContextMenu.new.emptyNameError");
			return null;
		}
		return name;
	}

	private String showChooseElementNameDialog(ElementType type, String defaultName) {
		String name = JOptionPane.showInputDialog(SwingUtilities.getRoot(this), TranslationService.getInstance().getString("treeContextMenu.new.element.chooseNameMessage"), defaultName);
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (name.length() == 0) {
			Utils.showErrorDialog("treeContextMenu.new.emptyNameError");
			return null;
		}
		if (containsName(type, name)) {
			Utils.showErrorDialog("treeContextMenu.new.inUseNameError");
			return null;
		}
		return name;
	}

	private boolean containsName(ElementType type, String name) {
		for (IdentifiedElement element : ContentPackEditionService.getInstance().listOf(type)) {
			if (element.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
