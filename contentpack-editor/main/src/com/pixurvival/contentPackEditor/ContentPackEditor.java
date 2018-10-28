package com.pixurvival.contentPackEditor;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.pixurvival.contentPackEditor.component.CPEMenuBar;
import com.pixurvival.contentPackEditor.component.ElementTypeChooser;
import com.pixurvival.contentPackEditor.component.ElementTypePanelCard;

public class ContentPackEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	public ContentPackEditor() {
		setSize(800, 600);

		setJMenuBar(new CPEMenuBar());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content = getContentPane();
		content.setLayout(new BorderLayout(10, 0));
		content.add(new ElementTypeChooser(), BorderLayout.WEST);
		content.add(new ElementTypePanelCard(), BorderLayout.CENTER);

		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new ContentPackEditor().setVisible(true);
	}
}
