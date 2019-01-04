package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.pixurvival.contentPackEditor.component.util.DocumentAdapter;
import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SearchPopup<T extends NamedElement> extends JDialog {

	public static final String ELEMENT_SELECTED_ACTION = "ELEMENT_SELECTED_ACTION";

	public static final int RESULT_SIZE = 6;

	private static final long serialVersionUID = 1L;

	@Getter
	@AllArgsConstructor
	private class ItemMatchEntry implements Comparable<ItemMatchEntry> {
		private T item;
		private int score;

		@Override
		public int compareTo(ItemMatchEntry o) {
			return score - o.score;
		}
	}

	private JTextField searchField = new JTextField(20);
	private JPanel resultPanel = new JPanel(new GridBagLayout());
	private @Getter @Setter Collection<T> items;
	private List<ItemMatchEntry> sortedList = new ArrayList<>();
	private Function<T, Icon> iconProvider;
	private SearchPopupSelectionModel selectionModel;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();

	public SearchPopup(Function<T, Icon> iconProvider) {
		super(JOptionPane.getRootFrame());
		this.iconProvider = iconProvider;
		setUndecorated(true);
		getRootPane().setBorder((Border) UIManager.get("PopupMenu.border"));
		Container content = getContentPane();
		selectionModel = new SearchPopupSelectionModel(resultPanel);
		JButton removeButton = new JButton("X");
		JPanel searchBar = new JPanel(new BorderLayout());
		searchBar.add(searchField, BorderLayout.CENTER);
		searchBar.add(removeButton, BorderLayout.EAST);
		content.setLayout(new BorderLayout(0, 2));
		content.add(searchBar, BorderLayout.NORTH);
		content.add(resultPanel, BorderLayout.CENTER);
		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				setVisible(false);
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
			}
		});
		searchField.getDocument().addDocumentListener(new DocumentAdapter(e -> updateSearchList()));
		searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), SearchPopupSelectionModel.NEXT_SELECTION_ACTION);
		searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), SearchPopupSelectionModel.PREVIOUS_SELECTION_ACTION);
		searchField.getActionMap().put(SearchPopupSelectionModel.NEXT_SELECTION_ACTION, selectionModel.getNextAction());
		searchField.getActionMap().put(SearchPopupSelectionModel.PREVIOUS_SELECTION_ACTION, selectionModel.getPreviousAction());
		searchField.addActionListener(e -> selectItem());
		removeButton.addActionListener(e -> remove());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		for (int i = 0; i < RESULT_SIZE; i++) {
			gbc.gridy = i;
			JMenuItem menuItem = new JMenuItem();
			resultPanel.add(menuItem, gbc);
			menuItem.setVisible(false);
			final int index = i;
			menuItem.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					selectionModel.setSelectedIndex(index);
				}
			});
			menuItem.addActionListener(e -> selectItem());
		}
		pack();
	}

	public void addItemSelectionListener(ValueChangeListener<T> l) {
		listeners.add(l);
	}

	public void show(Component relativeTo) {
		Point p = relativeTo.getLocationOnScreen();
		setLocation(p.x, p.y + relativeTo.getHeight());
		setVisible(true);
		searchField.requestFocus();
	}

	public void setSearchText(String text) {
		searchField.setText(text);
	}

	private void updateSearchList() {
		if (items == null) {
			return;
		}
		String searchText = searchField.getText();

		sortedList.clear();
		if (!searchText.isEmpty()) {
			for (T item : items) {
				int index;
				if ((index = item.getName().toLowerCase().indexOf(searchText.toLowerCase())) != -1) {
					sortedList.add(new ItemMatchEntry(item, index * 10000 + item.getName().length()));
				}
			}
		}
		sortedList.sort(null);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		for (int i = 0; i < sortedList.size() && i < RESULT_SIZE; i++) {
			gbc.gridy = i;
			T item = sortedList.get(i).getItem();
			JMenuItem menuItem = (JMenuItem) resultPanel.getComponent(i);
			menuItem.setText(item.getName());
			menuItem.setIcon(iconProvider.apply(item));
			menuItem.setVisible(true);
		}
		for (int i = sortedList.size(); i < RESULT_SIZE; i++) {
			JMenuItem menuItem = (JMenuItem) resultPanel.getComponent(i);
			menuItem.setVisible(false);
		}
		if (!sortedList.isEmpty()) {
			((JMenuItem) resultPanel.getComponent(0)).setArmed(true);
			selectionModel.setSelectedIndex(0);
		} else {
			selectionModel.setSelectedIndex(-1);
		}
		pack();
		revalidate();
	}

	private void selectItem() {
		if (selectionModel.getSelectedIndex() != -1) {
			T selectedItem = sortedList.get(selectionModel.getSelectedIndex()).getItem();
			listeners.forEach(l -> l.valueChanged(selectedItem));
			setVisible(false);
		}
	}

	private void remove() {
		listeners.forEach(l -> l.valueChanged(null));
		setVisible(false);
	}
}
