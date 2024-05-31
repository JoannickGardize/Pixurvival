package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.tooltip.RepresenterUtils;

public class TimeUI extends UIWindow {

    private long currentDay = 0;
    private long currentTimeSec = 0;

    public TimeUI() {
        super("time");
        Label dayCountLabel = new UILabel("hud.time.dayCount", Color.WHITE);
        Label dayCountValue = new Label("1", PixurvivalGame.getSkin(), "white") {
            @Override
            public void act(float delta) {
                long newDay = PixurvivalGame.getClient().getWorld().getTime().getDayCycle().getDayCount();
                if (newDay != currentDay) {
                    currentDay = newDay;
                    setText(String.valueOf(1 + newDay));
                }
            }
        };
        Label totalTimeLabel = new UILabel("hud.time.totalTime", Color.WHITE);
        Label totalTimeValue = new Label("00:00:00", PixurvivalGame.getSkin(), "white") {
            @Override
            public void act(float delta) {
                long time = PixurvivalGame.getClient().getWorld().getTime().getTimeMillis();
                if (time / 1000 != currentTimeSec) {
                    setText(RepresenterUtils.formatHoursMinutesSecondes(time));
                    currentTimeSec = time / 1000;
                }
            }
        };
        add(dayCountLabel).fill().align(Align.right).padLeft(5);
        add(dayCountValue).expand().fill().align(Align.left).padLeft(10);
        row();
        add(totalTimeLabel).fill().align(Align.right).padLeft(5);
        add(totalTimeValue).expand().fill().align(Align.left).padLeft(10);
        row();
        add(new DayCycleBar()).expand().fill().colspan(2);
        pack();
    }
}
