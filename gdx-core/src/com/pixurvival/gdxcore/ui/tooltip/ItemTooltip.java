package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.livingEntity.stats.StatListener;
import com.pixurvival.core.livingEntity.stats.StatValue;
import com.pixurvival.core.util.Cache;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.Separator;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.Getter;

import java.util.Locale;

public class ItemTooltip extends Table implements StatListener {

    public static final float ITEM_WIDTH = 20;

    private static final @Getter ItemTooltip instance = new ItemTooltip(true);

    private Item item;
    private boolean standalone;

    private Cache<String, Description> descriptionCache = new Cache<>(key -> {
        Locale locale = PixurvivalGame.getClient().getCurrentLocale();
        ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
        return DescriptionParser.parse(contentPack.getTranslation(locale, key));
    });

    public ItemTooltip(boolean standalone) {
        this.standalone = standalone;
        if (standalone) {
            setVisible(false);
        }
        setTouchable(Touchable.disabled);
        setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
        defaults().expand().fill().pad(2);
    }

    public void setItem(Item item) {
        if (this.item != item) {
            this.item = item;
            build();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (standalone) {
            DrawUtils.setTooltipPosition(this);
        }
        super.draw(batch, parentAlpha);
    }

    private void build() {
        this.clearChildren();
        Locale locale = PixurvivalGame.getClient().getCurrentLocale();
        ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
        Image image = new Image(PixurvivalGame.getContentPackTextures().getItem(item.getId()).getTexture());
        Label nameLabel = new Label(contentPack.getTranslation(locale, item, TranslationKey.NAME), PixurvivalGame.getSkin(), "white");
        add(image).expand(false, false).size(ITEM_WIDTH, ITEM_WIDTH);
        add(nameLabel);
        row();
        add(new Separator()).colspan(2).pad(0);
        row();
        Label typeLabel = new Label(PixurvivalGame.getString("itemType." + CaseUtils.pascalToCamelCase(item.getClass().getSimpleName())), PixurvivalGame.getSkin(), "white");
        add(typeLabel).colspan(2);
        row();
        add(ItemCharacteristicsRepresenter.represents(item)).colspan(2).pad(0);
        row();
        add(new Separator()).colspan(2).pad(0);
        row();
        Label descriptionLabel = new TooltipText(descriptionCache.get(TranslationKey.DESCRIPTION.getKey(item)).get());
        descriptionLabel.setWrap(true);
        add(descriptionLabel).colspan(2);
        invalidate();
        if (standalone) {
            pack();
        }
    }

    @Override
    public void statChanged(float oldValue, StatValue statValue) {
        if (isVisible()) {
            build();
        } else {
            item = null;
        }
    }
}
