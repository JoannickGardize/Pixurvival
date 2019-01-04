package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import lombok.Getter;

public class SearchPopupSelectionModel {

	public static final String NEXT_SELECTION_ACTION = "NEXT_SELECTION";
	public static final String PREVIOUS_SELECTION_ACTION = "PREVIOUS_SELECTION";

	private @Getter int selectedIndex = -1;

	private JComponent selectionComponent;

	private @Getter Action nextAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			forward(1);
		}
	};

	private @Getter Action previousAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			forward(-1);
		}

	};

	public SearchPopupSelectionModel(JComponent selectionComponent) {
		this.selectionComponent = selectionComponent;
	}

	public void setSelectedIndex(int index) {
		if (selectedIndex != -1) {
			((JMenuItem) selectionComponent.getComponent(selectedIndex)).setArmed(false);
		}
		selectedIndex = index;
		if (index != -1) {
			((JMenuItem) selectionComponent.getComponent(selectedIndex)).setArmed(true);
		}
	}

	private void forward(int forward) {
		setSelectedIndex(Math.floorMod((selectedIndex + forward), getSelectionLength()));
	}

	private int getSelectionLength() {
		for (int i = 0; i < selectionComponent.getComponentCount(); i++) {
			if (!selectionComponent.getComponent(i).isVisible()) {
				return i;
			}
		}
		return selectionComponent.getComponentCount();
	}
}
