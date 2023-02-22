package com.pixurvival.core.map.analytics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AreaTest {

    @Test
    public void encloseTest() {
        Area area = new Area(0, 0);
        area.enclose(new Position(1, 1));
        Assertions.assertEquals(new Area(0, 0, 1, 1), area);
        area.enclose(new Position(-1, -1));
        Assertions.assertEquals(new Area(-1, -1, 1, 1), area);
    }

    @Test
    public void enclosingWidthTest() {
        Area area = new Area(-1, -2, 1, 2);
        Assertions.assertEquals(2, area.enclosingWidth(new Position(0, 3)));
        Assertions.assertEquals(4, area.enclosingWidth(new Position(-3, 0)));
        Assertions.assertEquals(4, area.enclosingWidth(new Position(3, 5)));
    }

    @Test
    public void enclosingHeightTest() {
        Area area = new Area(-1, -2, 1, 2);
        Assertions.assertEquals(4, area.enclosingHeight(new Position(0, 0)));
        Assertions.assertEquals(5, area.enclosingHeight(new Position(-3, 3)));
        Assertions.assertEquals(5, area.enclosingHeight(new Position(0, -3)));
    }

    @Test
    public void widthTest() {
        Area area = new Area(-1, -2, 1, 2);
        Assertions.assertEquals(2, area.width());
    }

    @Test
    public void heightTest() {
        Area area = new Area(-1, -2, 1, 2);
        Assertions.assertEquals(4, area.height());
    }
}
