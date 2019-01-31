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
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementRemovedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RootElementList<E extends IdentifiedElement> extends JPanel {

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

	public RootElementList(ElementType elementType) {
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

		JButton addButton = new CPEButton("generic.add", this::addClick);
		JButton renameButton = new CPEButton("generic.rename", () -> {
			ElementEntry entry = list.getSelectedValue();
			if (entry != null) {
				String name = showChooseNameDialog();
				if (name != null) {
					entry.getElement().setName(name);
					repaint();
				}
			}
		});
		JButton removeButton = new CPEButton("generic.remove", () -> {
			ElementEntry entry = list.getSelectedValue();
			if (entry != null) {
				ContentPackEditionService.getInstance().removeElement(entry.getElement());
			}
		});
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(120, 0));
		add(scrollPane, gbc);
		gbc.gridy++;
		gbc.weighty = 0;
		add(addButton, gbc);
		gbc.gridy++;
		add(renameButton, gbc);
		gbc.gridy++;
		add(removeButton, gbc);
		EventManager.getInstance().register(this);
	}

	private void addClick() {
		String name = showChooseNameDialog();
		if (name != null) {
			ContentPackEditionService.getInstance().addElement(elementType, name);
		}
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

	public boolean isListValid() {
		DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
		for (int i = 0; i < model.size(); i++) {
			if (!model.get(i).valid || model.get(i).getElement().getId() != i) {
				return false;
			}
		}
		return true;
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void elementAdded(ElementAddedEvent event) {
		if (elementType.getElementClass() == event.getElement().getClass()) {
			DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
			model.addElement(new ElementEntry((E) event.getElement(), ElementType.of(event.getElement()).getElementEditor().isValueValid(event.getElement())));
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
	public void elementRemoved(ElementRemovedEvent event) {
		DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
		if (elementType.getElementClass() == event.getElement().getClass()) {
			for (int i = 0; i < model.size(); i++) {
				if (model.elementAt(i).getElement().equals(event.getElement())) {
					model.remove(i);
					break;
				}
			}
		}
		for (int i = 0; i < model.size(); i++) {
			ElementEntry entry = model.get(i);
			ElementEditor<IdentifiedElement> editor = ElementType.of(entry.getElement()).getElementEditor();
			entry.setValid(editor.isValueValid(entry.getElement()));
		}
		list.repaint();
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		DefaultListModel<ElementEntry> model = (DefaultListModel<ElementEntry>) list.getModel();
		model.clear();
		List<IdentifiedElement> elementList = ContentPackEditionService.getInstance().listOf(elementType);
		elementList.forEach(e -> model.addElement(new ElementEntry((E) e, ElementType.of(e).getElementEditor().isValueValid(e))));
	}

	private String showChooseNameDialog() {
		String name = JOptionPane.showInputDialog(SwingUtilities.getRoot(this), TranslationService.getInstance().getString("elementList.add.chooseNameMessage"), "");
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (name.length() == 0) {
			Utils.showErrorDialog("elementList.add.emptyNameError");
			return null;
		}
		if (contains(name)) {
			Utils.showErrorDialog("elementList.add.inUseNameError");
			return null;
		}
		return name;
	}
}
