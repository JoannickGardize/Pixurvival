package com.pixurvival.contentPackEditor;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ContentPackEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	public ContentPackEditor() {
		setJMenuBar(new CPEMenuBar());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(800, 600));
		pack();
		Context.getInstance().setFrame(this);
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
