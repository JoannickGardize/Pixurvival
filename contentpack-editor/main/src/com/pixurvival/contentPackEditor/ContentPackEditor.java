package com.pixurvival.contentPackEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;

import javax.swing.JFrame;

import com.pixurvival.contentPackEditor.component.CPEMenuBar;
import com.pixurvival.contentPackEditor.component.ElementTypeChooser;
import com.pixurvival.contentPackEditor.component.ElementTypePanelCard;
import com.pixurvival.core.util.ArgsUtils;

import lombok.Getter;

public class ContentPackEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	private static @Getter ContentPackEditor instance;

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
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (ClassNotFoundException | InstantiationException |
		// IllegalAccessException | UnsupportedLookAndFeelException e) {
		// e.printStackTrace();
		// }
		MainArgs mainArgs = ArgsUtils.readArgs(args, MainArgs.class);

		ContentPackEditor editor = new ContentPackEditor();
		instance = editor;
		instance.setVisible(true);
		if (mainArgs.getOpen() == null) {
			FileService.getInstance().newContentPack();
		} else {
			FileService.getInstance().open(new File(mainArgs.getOpen()));
		}
	}
}
