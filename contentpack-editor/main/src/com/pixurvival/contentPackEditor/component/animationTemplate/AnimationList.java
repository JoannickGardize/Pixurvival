package com.pixurvival.contentPackEditor.component.animationTemplate;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.util.CPEButton;
import com.pixurvival.contentPackEditor.component.util.EnumMapListModel;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;
import lombok.val;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// TODO make this an elementEditor
public class AnimationList extends JPanel {

    private static final long serialVersionUID = 1L;

    private JList<Animation> list = new JList<>(new EnumMapListModel<>(ActionAnimation.class));
    private JButton addButton = new CPEButton("generic.add", this::add);
    private JButton removeButton = new CPEButton("generic.remove", this::remove);
    private JButton addForCharacterButton = new CPEButton("animationList.addForCharacter", this::addForCharacter);
    private List<Consumer<Map<ActionAnimation, Animation>>> listChangedListener = new ArrayList<>();

    public AnimationList() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (((Animation) value).getFrames().isEmpty()) {
                    component.setForeground(Color.RED);
                }
                return component;
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(120, 0));
        add(scrollPane, gbc);
        gbc.weighty = 0;
        gbc.gridy++;
        add(addButton, gbc);
        gbc.gridy++;
        add(removeButton, gbc);
        gbc.gridy++;
        add(addForCharacterButton, gbc);
    }

    @SuppressWarnings("unchecked")
    public void setMap(Map<ActionAnimation, Animation> map) {
        val listModel = (EnumMapListModel<ActionAnimation, Animation>) list.getModel();
        listModel.setMap(map);
    }

    public Animation getSelectedValue() {
        return list.getSelectedValue();
    }

    public void addListChangedListener(Consumer<Map<ActionAnimation, Animation>> listener) {
        listChangedListener.add(listener);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }

    @SuppressWarnings("unchecked")
    public boolean isListValid() {
        EnumMapListModel<ActionAnimation, Animation> listModel = (EnumMapListModel<ActionAnimation, Animation>) list.getModel();
        for (val entry : listModel.entries()) {
            List<Frame> frames = entry.getValue().getFrames();
            if (frames == null || frames.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void add() {
        ActionAnimation actionAnimation = (ActionAnimation) JOptionPane.showInputDialog(SwingUtilities.getRoot(this),
                TranslationService.getInstance().getString("animationTemplateEditor.addAnimationMessage"), TranslationService.getInstance().getString("generic.add"),
                JOptionPane.PLAIN_MESSAGE, null, ActionAnimation.values(), ActionAnimation.MOVE_RIGHT);
        add(actionAnimation);
    }

    @SuppressWarnings("unchecked")
    private void add(ActionAnimation actionAnimation) {
        if (actionAnimation != null) {
            EnumMapListModel<ActionAnimation, Animation> listModel = (EnumMapListModel<ActionAnimation, Animation>) list.getModel();
            int index = listModel.put(actionAnimation, new Animation(actionAnimation));
            if (index != -1) {
                Map<ActionAnimation, Animation> map = listModel.toMap();
                listChangedListener.forEach(l -> l.accept(map));
                list.setSelectedIndex(index);
            }
        }
    }

    private void addForCharacter() {
        add(ActionAnimation.MOVE_RIGHT);
        add(ActionAnimation.MOVE_UP);
        add(ActionAnimation.MOVE_LEFT);
        add(ActionAnimation.MOVE_DOWN);
        add(ActionAnimation.STAND_RIGHT);
        add(ActionAnimation.STAND_UP);
        add(ActionAnimation.STAND_LEFT);
        add(ActionAnimation.STAND_DOWN);
        add(ActionAnimation.WORK_RIGHT);
        add(ActionAnimation.WORK_UP);
        add(ActionAnimation.WORK_DOWN);
        add(ActionAnimation.WORK_LEFT);
    }

    @SuppressWarnings("unchecked")
    private void remove() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            EnumMapListModel<ActionAnimation, Animation> listModel = (EnumMapListModel<ActionAnimation, Animation>) list.getModel();
            listModel.remove(selectedIndex);
            Map<ActionAnimation, Animation> map = listModel.toMap();
            listChangedListener.forEach(l -> l.accept(map));
        }
    }
}
