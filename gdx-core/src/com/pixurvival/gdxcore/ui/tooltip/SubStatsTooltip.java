package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.UIConstants;
import com.pixurvival.gdxcore.ui.UILabel;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.Getter;

public class SubStatsTooltip extends Tooltip {

    public static final float ITEM_WIDTH = 20;

    private static final @Getter SubStatsTooltip instance = new SubStatsTooltip();

    public SubStatsTooltip() {
        setTouchable(Touchable.disabled);
        setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
        defaults().expand().fill().align(Align.right).pad(2);
    }

    public void setVisibleForBaseStat(StatType statType) {
        setVisible(statType.getSubStats().length > 0);
        build(statType);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        DrawUtils.setTooltipPosition(this);
        super.draw(batch, parentAlpha);
    }

    private void build(StatType statType) {
        this.clearChildren();
        Color color = statType == StatType.STRENGTH ? UIConstants.STRENGTH_COLOR : UIConstants.AGILITY_COLOR;
        StatSet stats = PixurvivalGame.getClient().getMyPlayer().getStats();
        for (StatType subStat : statType.getSubStats()) {
            add(new UILabel("statType." + CaseUtils.upperToCamelCase(subStat.name()), " ", color));
            add(UILabel.rawText(RepresenterUtils.statValue(subStat, stats.getValue(subStat)), color));
            row();
        }
        invalidate();
        pack();
    }
}
