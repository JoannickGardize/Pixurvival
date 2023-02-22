package com.pixurvival.contentPackEditor.component.animationTemplate;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.AnimationPreview;
import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetChooserPreviewTabs;
import com.pixurvival.contentPackEditor.component.spriteSheet.SpriteSheetPreview.ClickEvent;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;

import javax.swing.*;
import java.awt.*;

public class AnimationEditor extends ElementEditor<Animation> {

    private static final long serialVersionUID = 1L;

    private VerticalListEditor<Frame> frameList = new VerticalListEditor<>(FrameEditor::new, Frame::new, VerticalListEditor.HORIZONTAL);
    private AnimationPreview animationPreview = new AnimationPreview();
    private SpriteSheetChooserPreviewTabs previewPanel = new SpriteSheetChooserPreviewTabs();

    public AnimationEditor() {
        super(Animation.class);
        FloatInput rotationEditor = new FloatInput();

        previewPanel.addTab(TranslationService.getInstance().getString("generic.animation"), animationPreview);
        TimeInput frameDurationInput = new TimeInput();
        frameList.setBorder(LayoutUtils.createGroupBorder("animationTemplateEditor.frames"));

        previewPanel.getSpriteSheetChooser().addValueChangeListener(s -> animationPreview.setSpriteSheet(s));

        previewPanel.getSpriteSheetPreview().addInteractionListener(o -> {
            if (o instanceof ClickEvent) {
                ClickEvent clickEvent = (ClickEvent) o;
                frameList.add(new Frame(clickEvent.getSpriteX(), clickEvent.getSpriteY()));
            }
        });
        bind(frameList, "frames");
        bind(frameDurationInput, "frameDuration");
        bind(rotationEditor, "rotationPerSecond");

        setLayout(new BorderLayout());
        JPanel editionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        gbc.insets.top = 5;
        LayoutUtils.addHorizontalLabelledItem(editionPanel, "animationTemplateEditor.frameDuration", frameDurationInput, gbc);
        LayoutUtils.addHorizontalLabelledItem(editionPanel, "animationTemplateEditor.rotationPerSecond", rotationEditor, gbc);
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.weightx = 1;
        editionPanel.add(frameList, gbc);

        add(editionPanel, BorderLayout.WEST);
        add(previewPanel, BorderLayout.CENTER);
    }

    @Override
    public boolean isValueValid(Animation value) {
        return super.isValueValid(value) && value.getFrames() != null && !value.getFrames().isEmpty();
    }

    @Override
    protected void valueChanged(ValueComponent<?> source) {
        animationPreview.setAnimation(getValue());
    }
}
