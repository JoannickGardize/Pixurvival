package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.*;
import com.pixurvival.contentPackEditor.util.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ResourcesDialog extends EditorDialog {

    private static final long serialVersionUID = 1L;

    private JList<ResourceEntry> resourceList = new JList<>(new DefaultListModel<>());
    private ResourcePreview resourcePreview = new ResourcePreview();

    private JPanel buttonPanel = new JPanel();

    public ResourcesDialog() {
        super("resourcesDialog.title");
        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(resourceList);
        JPanel listPanel = (JPanel) LayoutUtils.addBorder(scrollPane, 0, 0, 0, 0);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        resourcePreview.setPreferredSize(new Dimension(400, 500));
        content.add(listPanel, BorderLayout.WEST);
        content.add(resourcePreview, BorderLayout.CENTER);

        resourceList.addListSelectionListener(e -> {
            if (resourceList.getSelectedValue() == null) {
                resourcePreview.setObject(null);
            } else {
                resourcePreview.setObject(resourceList.getSelectedValue().getPreview());
            }
            resourcePreview.repaint();
        });

        EventManager.getInstance().register(this);
        pack();
    }

    @EventListener
    public void contentPackLoaded(ContentPackLoadedEvent event) {
        DefaultListModel<ResourceEntry> model = getResourceListModel();
        model.clear();
        for (ResourceEntry entry : ResourcesService.getInstance().getResources()) {
            model.addElement(entry);
        }
        rebuildButtons();
    }

    @EventListener
    public void contentPackSaved(ContentPackSavedEvent event) {
        rebuildButtons();
    }

    @EventListener
    public void resourceAdded(ResourceAddedEvent event) {
        int index = 0;
        DefaultListModel<ResourceEntry> list = getResourceListModel();
        boolean inserted = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.getElementAt(i).getName().compareTo(event.getResourceName()) > 0) {
                list.add(i, ResourcesService.getInstance().getResource(event.getResourceName()));
                inserted = true;
                break;
            }
        }
        if (!inserted) {
            list.addElement(ResourcesService.getInstance().getResource(event.getResourceName()));
        }
    }

    @EventListener
    public void resourceChanged(ResourceChangedEvent event) {
        DefaultListModel<ResourceEntry> model = getResourceListModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getName().equals(event.getResourceName())) {
                model.set(i, ResourcesService.getInstance().getResource(event.getResourceName()));
                resourceList.setSelectedIndex(i);
                resourcePreview.setObject(resourceList.getSelectedValue().getPreview());
                resourcePreview.repaint();
                break;
            }
        }
    }

    @EventListener
    public void resourceRemoved(ResourceRemovedEvent event) {
        DefaultListModel<ResourceEntry> model = getResourceListModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getName().equals(event.getResourceName())) {
                model.remove(i);
                break;
            }
        }
    }

    @EventListener
    public void resourceRenamed(ResourceRenamedEvent event) {
        resourceRemoved(new ResourceRemovedEvent(event.getOldResourceName()));
        resourceAdded(new ResourceAddedEvent(event.getNewResourceName()));
    }

    @Override
    public void setVisible(boolean b) {
        setLocationRelativeTo(getOwner());
        super.setVisible(b);
    }

    private void remove() {
        int selectedIndex = resourceList.getSelectedIndex();
        if (selectedIndex != -1) {
            ResourcesService.getInstance().deleteResource(resourceList.getSelectedValue().toString());
        }
    }

    private void rename() {
        int selectedIndex = resourceList.getSelectedIndex();
        if (selectedIndex != -1) {
            ResourcesService.getInstance().renameResource(resourceList.getSelectedValue().toString());
        }
    }

    private void rebuildButtons() {
        if (FileService.getInstance().isDirectoryMode()) {
            buildButtonsForDirectoryMode();
        } else {
            buildButtonsForZipMode();
        }
    }

    private void buildButtonsForZipMode() {
        JButton addButton = new CPEButton("generic.add", () -> ResourcesService.getInstance().addResource());
        JButton modifyButton = new CPEButton("generic.modify", () -> {
            ResourceEntry entry = resourceList.getSelectedValue();
            if (entry != null) {
                ResourcesService.getInstance().addResource(entry.getName());
            }
        });
        JButton importButton = new CPEButton("resources.importFolder", () -> ResourcesService.getInstance().importFolder());
        JButton deleteButton = new CPEButton("generic.remove", this::remove);
        JButton renameButton = new CPEButton("generic.rename", this::rename);
        buttonPanel.removeAll();
        LayoutUtils.addVertically(buttonPanel, addButton, modifyButton, renameButton, deleteButton, importButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void buildButtonsForDirectoryMode() {
        JButton refreshButton = new CPEButton("resources.refresh", () -> {
            ResourcesService.getInstance().clearAndNotify();
            ResourcesService.getInstance().importFolder(FileService.getInstance().getCurrentResourcesDirectory());
        });
        JButton openFolderButton = new CPEButton("resources.openFolder", () -> {
            try {
                Desktop.getDesktop().open(FileService.getInstance().getCurrentResourcesDirectory());
            } catch (IOException e) {
                e.printStackTrace();
                DialogUtils.showErrorDialog(e);
            }
        });
        buttonPanel.removeAll();
        LayoutUtils.addVertically(buttonPanel, refreshButton, openFolderButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private DefaultListModel<ResourceEntry> getResourceListModel() {
        return (DefaultListModel<ResourceEntry>) resourceList.getModel();
    }
}
