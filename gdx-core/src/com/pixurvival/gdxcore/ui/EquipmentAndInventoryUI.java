package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;
import com.pixurvival.gdxcore.ui.tooltip.SubStatsTooltip;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class EquipmentAndInventoryUI extends UIWindow {

    private static final Color SEPARATOR_COLOR = new Color(0.588f, 0.588f, 0.588f, 1f);

    private Label strengthLabel;
    private Label agilityLabel;
    private Label intelligenceLabel;
    private Set<Actor> touchActors = new HashSet<>();
    private @Getter InventoryTable inventoryTable;

    public EquipmentAndInventoryUI() {
        super("equipment");
        InventoryTable equipmentTable = createEquipmentTable();

        Table statTable = createStatTable();

        Table mainTable = new Table();
        mainTable.add(equipmentTable).expand().fill();
        mainTable.add(statTable).fill();
        add(mainTable).expand().fill().padRight(5);
        row();

        addInventoryTitle();

        addInventory();

        pack();
    }

    private Table createStatTable() {
        Table statTable = new Table();
        statTable.defaults().fill().align(Align.right).minWidth(40);
        addSubStatDisplayListener(statTable.add(new UILabel("statType.strength", " ", UIConstants.STRENGTH_COLOR)).getActor(), StatType.STRENGTH);
        strengthLabel = new UILabel(UIConstants.STRENGTH_COLOR);
        addSubStatDisplayListener(strengthLabel, StatType.STRENGTH);
        statTable.add(strengthLabel);
        statTable.row();
        addSubStatDisplayListener(statTable.add(new UILabel("statType.agility", " ", UIConstants.AGILITY_COLOR)).getActor(), StatType.AGILITY);
        agilityLabel = new UILabel(UIConstants.AGILITY_COLOR);
        addSubStatDisplayListener(agilityLabel, StatType.AGILITY);
        statTable.add(agilityLabel);
        statTable.row();
        statTable.add(new UILabel("statType.intel", " ", UIConstants.INTELLIGENCE_COLOR));
        intelligenceLabel = new UILabel(UIConstants.INTELLIGENCE_COLOR);
        statTable.add(intelligenceLabel);
        statTable.row();
        return statTable;
    }

    private InventoryTable createEquipmentTable() {
        Inventory inventory = new Inventory(4);
        InventoryTable equipmentTable = new InventoryTable(inventory, 4) {
            @Override
            public Actor newSlot(Inventory inventory, int index, int actionIndex) {
                switch (index) {
                    case 0:
                        return new EquipmentSlot(Equipment.CLOTHING_INDEX);
                    case 1:
                        return new EquipmentSlot(Equipment.WEAPON_INDEX);
                    case 2:
                        return new EquipmentSlot(Equipment.ACCESSORY1_INDEX);
                    case 3:
                        return new EquipmentSlot(Equipment.ACCESSORY2_INDEX);
                    default:
                        return null;
                }
            }
        };
        return equipmentTable;
    }

    private void addInventory() {
        Inventory inv = PixurvivalGame.getClient().getMyInventory();
        inventoryTable = new InventoryTable(inv, 8) {
            @Override
            public Actor newSlot(Inventory inventory, int index, int actionIndex) {
                InventorySlot slot = new InventorySlot(inventory, index, actionIndex);
                if (index < 10) {
                    InputAction action = InputAction.valueOf("INVENTORY" + (index + 1));
                    slot.setShortcutDrawer(new ShortcutDrawer(slot, action, ShortcutDrawer.BOTTOM));
                }
                return slot;
            }
        };
        add(inventoryTable).expand().fill();
    }

    private void addInventoryTitle() {
        Label inventoryLabel = newLabel(PixurvivalGame.getString("hud.inventory.title"),
                PixurvivalGame.getSkin().get("title", Label.LabelStyle.class));
        inventoryLabel.setEllipsis(true);
        add(inventoryLabel).growX();
        row();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        StatSet stats = PixurvivalGame.getClient().getMyPlayer().getStats();
        strengthLabel.setText(PixurvivalGame.DECIMAL_FORMAT.format(stats.getValue(StatType.STRENGTH)));
        agilityLabel.setText(PixurvivalGame.DECIMAL_FORMAT.format(stats.getValue(StatType.AGILITY)));
        intelligenceLabel.setText(PixurvivalGame.DECIMAL_FORMAT.format(stats.getValue(StatType.INTELLIGENCE)));
        super.draw(batch, parentAlpha);
    }

    private void addSubStatDisplayListener(Actor actor, StatType statType) {
        touchActors.add(actor);
        actor.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SubStatsTooltip.getInstance().setVisibleForBaseStat(statType);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                SubStatsTooltip.getInstance().setVisible(false);
            }
        });
    }

}
