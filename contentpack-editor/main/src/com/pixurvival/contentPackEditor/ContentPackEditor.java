package com.pixurvival.contentPackEditor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.pixurvival.contentPackEditor.component.CPEMenuBar;
import com.pixurvival.contentPackEditor.component.ElementTypePanelCard;
import com.pixurvival.contentPackEditor.component.tree.LayoutTree;
import com.pixurvival.contentPackEditor.event.ContentPackLoadedEvent;
import com.pixurvival.contentPackEditor.event.ContentPackSavedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.settings.Settings;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.ReleaseVersion;

import lombok.Getter;

public class ContentPackEditor extends JFrame {

	private static final String TITLE_PREFFIX = "ContentPack Editor - ";
	private static final long serialVersionUID = 1L;

	private static @Getter ContentPackEditor instance;

	public ContentPackEditor() {
		EventManager.getInstance().register(this);
		setSize(1300, 800);

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
		setIconImage(ImageService.getInstance().get("icon"));
	}

	public static void main(String[] args) {
		String language = Locale.getDefault().getLanguage();
		if (!"fr".equals(language) && !"en".equals(language)) {
			Locale.setDefault(Locale.ENGLISH);
		}
		MainArgs mainArgs = ArgsUtils.readArgs(args, MainArgs.class);
		FileService.getInstance().initialize(new File(mainArgs.getContentPackDirectory()));
		run(mainArgs);
		instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		instance.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if (FileService.getInstance().savePrevious()) {
					System.exit(0);
				}
			}
		});
	}

	public static void run(MainArgs mainArgs) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Settings.getInstance().apply(null);
		ContentPackEditor editor = new ContentPackEditor();
		instance = editor;
		instance.setVisible(true);
		if (mainArgs.getOpen() == null) {
			FileService.getInstance().newContentPack();
		} else {
			FileService.getInstance().open(new File(mainArgs.getOpen()));
		}
	}

	@EventListener
	public void contentPackLoaded(ContentPackLoadedEvent event) {
		updateWindowTitle();
	}

	@EventListener
	public void contentPackSaved(ContentPackSavedEvent event) {
		updateWindowTitle();
	}

	private void updateWindowTitle() {
		File currentFile = FileService.getInstance().getCurrentFile();
		if (currentFile == null) {
			setTitle(TITLE_PREFFIX + ReleaseVersion.getActual().displayName() + " - " + TranslationService.getInstance().getString("menuBar.file.new"));
		} else {
			try {
				setTitle(TITLE_PREFFIX + ReleaseVersion.getActual().displayName() + " - " + currentFile.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
