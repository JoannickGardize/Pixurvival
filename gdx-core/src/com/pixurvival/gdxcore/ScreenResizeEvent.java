package com.pixurvival.gdxcore;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ScreenResizeEvent extends Event {

    private Viewport viewport;
}
