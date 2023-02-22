package com.pixurvival.contentPackEditor.component.tile;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.component.AnimationPreview;
import com.pixurvival.contentPackEditor.component.constraint.UnitSpriteFrameConstraint;
import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.contentPackEditor.event.ContentPackConstantChangedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import javax.swing.*;
import java.awt.*;

public class TileEditor extends RootElementEditor<Tile> {

    private static final long serialVersionUID = 1L;

    private AnimationPreview imagePreview = new AnimationPreview();
    private UnitSpriteFrameConstraint frameConstraint = new UnitSpriteFrameConstraint();

    public TileEditor() {
        super(Tile.class);

        // Construction
        ElementChooserButton<ResourceEntry> imageField = new ElementChooserButton<>(ResourcesService.getInstance().getResourcesSupplier());
        EventManager.getInstance().register(this);
        VerticalListEditor<Frame> frameList = new VerticalListEditor<>(() -> {
            FrameEditor frame = new FrameEditor();
            frame.setAdditionalConstraint(frameConstraint);
            return frame;
        }, Frame::new, VerticalListEditor.HORIZONTAL);
        BooleanCheckBox solidCheckBox = new BooleanCheckBox();
        PercentInput velocityFactorInput = new PercentInput();
        imagePreview.setAnimation(new Animation());
        SpriteSheet tileSpriteSheet = new SpriteSheet();
        tileSpriteSheet.setWidth(GameConstants.PIXEL_PER_UNIT);
        tileSpriteSheet.setHeight(GameConstants.PIXEL_PER_UNIT);
        imagePreview.setSpriteSheet(tileSpriteSheet);
        LayoutUtils.setMinimumSize(imagePreview, 32, 32);

        // Binding
        bind(imageField, "image").getter(v -> v.getImage() == null ? null : ResourcesService.getInstance().getResource(v.getImage())).setter((v, f) -> v.setImage(f == null ? null : f.getName()));
        bind(frameList, "frames");
        bind(solidCheckBox, "solid");
        bind(velocityFactorInput, "velocityFactor");

        // Layouting
        setLayout(new BorderLayout(10, 5));

        JPanel leftPanel = LayoutUtils.createVerticalBox(10, 2, imagePreview, LayoutUtils.labelled("generic.image", imageField), frameList);
        leftPanel.setBorder(LayoutUtils.createGroupBorder("generic.image"));
        add(leftPanel, BorderLayout.WEST);
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
        LayoutUtils.addHorizontalLabelledItem(centerPanel, "generic.solid", solidCheckBox, gbc);
        LayoutUtils.addHorizontalLabelledItem(centerPanel, "tileEditor.velocityFactor", velocityFactorInput, gbc);
        add(centerPanel, BorderLayout.CENTER);

    }

    @Override
    protected void valueChanged(ValueComponent<?> source) {
        imagePreview.getAnimation().setFrames(getValue().getFrames());
        imagePreview.getSpriteSheet().setImage(getValue().getImage());
    }

    @EventListener
    public void contentPackConstantChangedEvent(ContentPackConstantChangedEvent event) {
        imagePreview.getAnimation().setFrameDuration(event.getConstants().getTileAnimationSpeed());
    }

    @Override
    public void setValue(Tile value, boolean sneaky) {
        frameConstraint.setElement(value);
        super.setValue(value, sneaky);
    }

    @Override
    public boolean isValueValid(Tile value) {
        frameConstraint.setElement(value);
        boolean valid = super.isValueValid(value);
        frameConstraint.setElement(getValue());
        return valid;
    }
}
