package com.pixurvival.contentPackEditor.component.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.IconService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.event.ElementRenamedEvent;
import com.pixurvival.contentPackEditor.event.ElementSelectedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.util.DialogUtils;
import com.pixurvival.contentPackEditor.util.MenuBuilder;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.util.CaseUtils;

public class LayoutTree extends JTree {

	private static final long serialVersionUID = 1L;

	private int currentContextX;
	private int currentContextY;

	public LayoutTree() {
		EventManager.getInstance().register(this);
		getActionMap().put("cut", null);
		getActionMap().put("copy", null);
		getActionMap().put("paste", null);
		getActionMap().getParent().put("cut", null);
		getActionMap().getParent().put("copy", null);
		getActionMap().getParent().put("paste", null);

		JPopupMenu contextMenu = new JPopupMenu();
		MenuBuilder menuBuilder = new MenuBuilder(contextMenu, "treeContextMenu");
		menuBuilder.addItem("new.folder", this::newFolder);
		for (ElementType type : ElementType.values()) {
			menuBuilder.addItem("new." + CaseUtils.upperToCamelCase(type.name()), () -> this.newElement(type), type.toString(), IconService.getInstance().get(type));
		}
		menuBuilder.addItem("rename", this::rename);
		menuBuilder.addItem("delete", this::delete);

		setModel(new LayoutTreeModel());
		setCellRenderer(new LayoutTreeCellRenderer());
		setRootVisible(false);
		setShowsRootHandles(true);
		setDragEnabled(true);
		setDropMode(DropMode.ON_OR_INSERT);
		setTransferHandler(new TreeTransferHandler());
		getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

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

		addTreeSelectionListener(e -> {
			if (e.isAddedPath()) {
				TreePath path = e.getPath();
				if (path.getLastPathComponent() instanceof LayoutElement) {
					LayoutElement layoutElement = (LayoutElement) path.getLastPathComponent();
					EventManager.getInstance().fire(new ElementSelectedEvent(layoutElement.getElement()));
				}
			}
		});
	}

	private LayoutNode getRightClickedNode() {
		TreePath path = getClosestPathForLocation(currentContextX, currentContextY);
		return path == null ? LayoutManager.getInstance().getRoot() : (LayoutNode) path.getLastPathComponent();

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
		NamedIdentifiedElement newElement = ContentPackEditionService.getInstance().addElement(type, name);
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

	private void rename() {
		LayoutNode selectedNode = getRightClickedNode();
		if (selectedNode instanceof LayoutFolder) {
			String newName = showChooseFolderNameDialog(((LayoutFolder) selectedNode).getName());
			if (newName != null) {
				((LayoutFolder) selectedNode).setName(newName);
				((LayoutTreeModel) getModel()).notifyNodeChanged(selectedNode);
			}
		} else {
			LayoutElement layoutElement = (LayoutElement) selectedNode;
			String newName = showChooseElementNameDialog(ElementType.of(layoutElement.getElement()), layoutElement.getElement().getName());
			if (newName != null) {
				String oldName = layoutElement.getElement().getName();
				layoutElement.getElement().setName(newName);
				((LayoutTreeModel) getModel()).notifyNodeChanged(selectedNode);
				EventManager.getInstance().fire(new ElementRenamedEvent(oldName, layoutElement.getElement()));
			}
		}
	}

	private void delete() {
		LayoutNode selectedNode = getRightClickedNode();

		String messageKey = selectedNode instanceof LayoutFolder ? "treeContextMenu.delete.folder" : "treeContextMenu.delete.element";
		int option = JOptionPane.showConfirmDialog(null, TranslationService.getInstance().getString(messageKey).replace("{0}", selectedNode.toString()));
		if (option != JOptionPane.YES_OPTION) {
			return;
		}

		if (selectedNode instanceof LayoutFolder) {
			selectedNode.forEachLeaf(childNode -> ContentPackEditionService.getInstance().removeElement(((LayoutElement) childNode).getElement()));
		} else {
			ContentPackEditionService.getInstance().removeElement(((LayoutElement) selectedNode).getElement());
		}
		LayoutTreeModel model = ((LayoutTreeModel) getModel());
		model.remove(selectedNode);
	}

	private String showChooseFolderNameDialog(String defaultName) {
		String name = JOptionPane.showInputDialog(SwingUtilities.getRoot(this), TranslationService.getInstance().getString("treeContextMenu.new.folder.chooseNameMessage"), defaultName);
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (name.length() == 0) {
			DialogUtils.showErrorDialog("treeContextMenu.new.emptyNameError");
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
			DialogUtils.showErrorDialog("treeContextMenu.new.emptyNameError");
			return null;
		}
		if (containsName(type, name)) {
			DialogUtils.showErrorDialog("treeContextMenu.new.inUseNameError");
			return null;
		}
		return name;
	}

	private boolean containsName(ElementType type, String name) {
		for (NamedIdentifiedElement element : ContentPackEditionService.getInstance().listOf(type)) {
			if (element.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
