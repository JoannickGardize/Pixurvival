package com.pixurvival.contentPackEditor.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionListener;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.Utils;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.EventHandler;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedElement;

public class ElementList<E extends NamedElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private ElementType elementType;
	private JList<E> list;
	private JButton addButton;
	private JButton removeButton;

	public ElementList(ElementType elementType) {
		this.elementType = elementType;
		list = new JList<>(new DefaultListModel<>());

		addButton = new CPEButton("elementList.add", () -> addClick());
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(120, 0));
		add(scrollPane, gbc);
		gbc.gridy++;
		gbc.weighty = 0;
		add(addButton, gbc);
		EventManager.getInstance().register(this);
	}

	private void addClick() {
		String name = JOptionPane
				.showInputDialog(TranslationService.getInstance().getString("elementList.add.chooseNameMessage"), "");
		if (name == null) {
			return;
		}
		name = name.trim();
		if (name.length() == 0) {
			Utils.showErrorDialog("elementList.add.emptyNameError");
			return;
		}
		if (contains(name)) {
			Utils.showErrorDialog("elementList.add.inUseNameError");
			return;
		}
		ContentPackEditionService.getInstance().addElement(elementType, name);
	}

	public boolean contains(String name) {
		ListModel<E> model = list.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void elementAdded(ElementAddedEvent event) {
		if (elementType.getElementClass() == event.getElement().getClass()) {
			DefaultListModel<E> model = (DefaultListModel<E>) list.getModel();
			model.addElement((E) event.getElement());
		}
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		list.addListSelectionListener(listener);
	}
}
