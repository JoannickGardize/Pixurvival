package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.Utils;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ElementList<E extends NamedElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@AllArgsConstructor
	private class ElementEntry {
		private E element;
		private boolean valid;

		@Override
		public String toString() {
			return element.toString();
		}
	}

	private ElementType elementType;
	private JList<ElementEntry> list;
	private JButton addButton;
	private JButton removeButton;

	public ElementList(ElementType elementType) {
		this.elementType = elementType;
		list = new JList<>(new DefaultListModel<>());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unchecked")
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (!((ElementEntry) value).isValid()) {
					component.setForeground(Color.RED);
				}
				return component;
			}
		});

		addButton = new CPEButton("generic.add", () -> addClick());
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
		String name = JOptionPane.showInputDialog(SwingUtilities.getRoot(this), TranslationService.getInstance().getString("elementList.add.chooseNameMessage"), "");
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
		ListModel<ElementEntry> model = list.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i).getElement().getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public E getSelectedElement() {
		if (list.getSelectedIndex() == -1) {
			return null;
		} else {
			return list.getSelectedValue().getElement();
		}
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		list.addListSelectionListener(listener);
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void elementAdded(ElementAddedEvent event) {
		if (elementType.getElementClass() == event.getElement().getClass()) {
			DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
			model.addElement(new ElementEntry((E) event.getElement(), false));
			list.setSelectedIndex(model.getSize() - 1);
		}
	}

	@EventListener
	public void elementChanged(ElementChangedEvent event) {
		if (elementType.getElementClass() == event.getElement().getClass()) {
			DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
			int index = event.getElement().getId();
			if (index < model.getSize()) {
				model.get(index).setValid(event.isValid());
			}
			list.repaint();
		}
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
		model.clear();
		List<NamedElement> elementList = ContentPackEditionService.getInstance().listOf(elementType);
		elementList.forEach(e -> model.addElement(new ElementEntry((E) e, ContentPackEditionService.getInstance().isValid(e))));
	}

}
