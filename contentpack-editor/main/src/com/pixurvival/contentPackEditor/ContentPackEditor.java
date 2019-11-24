package com.pixurvival.contentPackEditor;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.pixurvival.contentPackEditor.component.CPEMenuBar;
import com.pixurvival.contentPackEditor.component.ElementTypePanelCard;
import com.pixurvival.contentPackEditor.component.tree.LayoutTree;
import com.pixurvival.core.util.ArgsUtils;

import lombok.Getter;

public class ContentPackEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	private static @Getter ContentPackEditor instance;

	public ContentPackEditor() {
		setSize(1300, 900);

		setJMenuBar(new CPEMenuBar());

		JScrollPane treeScrollPane = new JScrollPane(new LayoutTree());
		treeScrollPane.setMinimumSize(new Dimension(150, 300));
		treeScrollPane.setPreferredSize(new Dimension(400, 600));
		treeScrollPane.setMaximumSize(new Dimension(1000, 2000));
		ElementTypePanelCard card = new ElementTypePanelCard();
		card.setMinimumSize(new Dimension(150, 300));
		card.setPreferredSize(new Dimension(500, 600));
		card.setMaximumSize(new Dimension(2000, 2000));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, card);
		setContentPane(splitPane);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		MainArgs mainArgs = ArgsUtils.readArgs(args, MainArgs.class);
		run(mainArgs);
		instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void run(MainArgs mainArgs) {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (ClassNotFoundException | InstantiationException |
		// IllegalAccessException | UnsupportedLookAndFeelException e) {
		// e.printStackTrace();
		// }

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
