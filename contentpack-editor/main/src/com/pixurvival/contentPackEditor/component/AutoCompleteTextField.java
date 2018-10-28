package com.pixurvival.contentPackEditor.component;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AutoCompleteTextField<T extends NamedElement> extends JPanel {

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

	private static final long serialVersionUID = 1L;

	private JTextField textField = new JTextField(20);
	private JPopupMenu suggestionMenu = new JPopupMenu();

	private Collection<T> items;

	private List<ItemMatchEntry> sortList = new ArrayList<>();

	private Robot robot;

	private Function<T, ImageIcon> iconProvider;

	public AutoCompleteTextField(Function<T, ImageIcon> iconProvider) {
		this.iconProvider = iconProvider;
		add(textField);
		textField.getDocument().addDocumentListener(new DocumentAdapter(e -> updateSuggestion()));
		textField.setFocusTraversalPolicy(suggestionMenu.getFocusTraversalPolicy());
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	public void setItems(Collection<T> newItems) {
		items = newItems;
	}

	protected void updateSuggestion() {
		if (items == null || items.isEmpty()) {
			return;
		}
		String searchText = textField.getText();
		if (searchText.isEmpty()) {
			return;
		}
		sortList.clear();
		for (T item : items) {
			int index;
			if ((index = item.getName().indexOf(searchText)) != -1) {
				sortList.add(new ItemMatchEntry(item, index * 10000 + item.getName().length()));
			}
		}
		sortList.sort(null);
		suggestionMenu.removeAll();
		for (int i = 0; i < sortList.size() && i < 8; i++) {
			T item = sortList.get(i).getItem();
			suggestionMenu.add(new JMenuItem(item.getName(), iconProvider.apply(item)));

		}
		suggestionMenu.pack();
		suggestionMenu.revalidate();
		if (!suggestionMenu.isVisible()) {
			suggestionMenu.show(textField, 0, textField.getHeight());
		}

		textField.requestFocus();
		if (suggestionMenu.getComponentCount() > 0) {
			suggestionMenu.getSelectionModel().setSelectedIndex(0);
		}
		robot.keyPress(KeyEvent.VK_DOWN);
	}
}
