package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ElementAddedEvent;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;

public class ElementTypeChooser extends JList<ElementType> {

	private static final long serialVersionUID = 1L;

	public ElementTypeChooser() {
		super(ElementType.values());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				EventManager.getInstance().fire(new ElementTypeChooseEvent(getSelectedValue()));
			}
		});
		setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (!ContentPackEditionService.getInstance().isAllValid((ElementType) value)) {
					component.setForeground(Color.RED);
				}
				return component;
			}
		});
		EventManager.getInstance().register(this);
	}

	@EventListener
	public void elementChanged(ElementChangedEvent event) {
		repaint();
	}

	public void elementAdded(ElementAddedEvent event) {
		repaint();
	}
}
