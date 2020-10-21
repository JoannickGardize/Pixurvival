package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.ContentPackContext;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;

public class ContentPackChooserDialog extends EditorDialog {

	private static final long serialVersionUID = 1L;

	private JList<ContentPackSummary> list = new JList<>(new DefaultListModel<>());

	public ContentPackChooserDialog() {
		super("contentPackChooserDialog.title");
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getContentPane().setLayout(new BorderLayout(5, 5));
		getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
		CPEButton openButton = new CPEButton("menuBar.file.open", () -> {
			ContentPackSummary cp = list.getSelectedValue();
			if (cp != null) {
				setVisible(false);
				try {
					FileService.getInstance().savePrevious();
					FileService.getInstance().open(FileService.getInstance().getContentPackContext().fileOf(cp.getIdentifier()));
				} catch (ContentPackException e) {
					e.printStackTrace();
				}
			}
		});
		getContentPane().add(LayoutUtils.single(openButton), BorderLayout.SOUTH);
		setSize(200, 400);
	}

	@Override
	public void setVisible(boolean b) {
		ContentPackContext context = FileService.getInstance().getContentPackContext();
		context.refreshList();
		List<ContentPackSummary> summaryList = context.list();
		DefaultListModel<ContentPackSummary> model = (DefaultListModel<ContentPackSummary>) list.getModel();
		model.clear();
		summaryList.forEach(model::addElement);
		super.setVisible(b);
	}
}
