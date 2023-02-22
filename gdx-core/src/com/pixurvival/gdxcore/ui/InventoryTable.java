package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.item.Inventory;
import lombok.Getter;

// TODO abstraction of inventory for craft
public class InventoryTable extends Table {

    private @Getter Inventory inventory;
    private @Getter int rowCount;
    private @Getter int rowLength;

    public InventoryTable(Inventory inventory, int rowLength) {
        this(inventory, rowLength, 0);
    }

    public InventoryTable(Inventory inventory, int rowLength, int actionIndexOffset) {
        this.inventory = inventory;
        this.rowLength = rowLength;
        int size = inventory.size();
        rowCount = size / rowLength;
        if (size % rowLength > 0) {
            rowCount++;
        }
        defaults().fill().minSize(10).maxSize(60).prefSize(60).padLeft(-1).padTop(-1);
        for (int i = 0; i < inventory.size(); i++) {
            add(newSlot(inventory, i, i + actionIndexOffset));
            if ((i + 1) % rowLength == 0) {
                row();
            }
        }
    }

    @Override
    public void layout() {
        sizeToFill(getWidth(), getHeight());
        super.layout();
    }

    @SuppressWarnings("unchecked")
    public void sizeToFill(float width, float height) {
        float slotSize = Math.min(width / rowLength, height / rowCount);
        for (Cell<Actor> cell : getCells()) {
            cell.prefSize(slotSize);
        }
    }

    /**
     * Allow override of slots actor type
     *
     * @param inventory
     * @param index
     * @return
     */
    public Actor newSlot(Inventory inventory, int index, int actionIndex) {
        return new InventorySlot(inventory, index, actionIndex);
    }

    @SuppressWarnings("rawtypes")
    public void setCellsPrefSize(float size) {
        for (Cell cell : getCells()) {
            cell.size(size);
        }
    }

    public float getActualCellSize() {
        if (hasChildren()) {
            return getChild(0).getWidth();
        } else {
            return 0;
        }
    }
}
