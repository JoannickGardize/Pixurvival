package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.ValidationService;
import com.pixurvival.core.contentPack.validation.ErrorNode;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ErrorListDialog extends EditorDialog {

    JList<ErrorNode> errorList = new JList<>(new DefaultListModel<>());

    public ErrorListDialog() {
        super("errorListDialog.title");
        setModal(false);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(errorList), BorderLayout.CENTER);
        setSize(800, 600);
    }

    private void refreshErrorList() {
        List<ErrorNode> errors = ValidationService.getInstance().getErrorList();
        DefaultListModel<ErrorNode> model = (DefaultListModel<ErrorNode>) errorList.getModel();
        model.clear();
        errors.forEach(model::addElement);
    }

    @Override
    public void setVisible(boolean b) {
        refreshErrorList();
        super.setVisible(b);
    }

    private static final long serialVersionUID = 1L;

}
