package com.pixurvival.contentPackEditor.component.effect;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.*;
import com.pixurvival.core.alteration.condition.*;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.FloatComparison;

import java.util.ArrayList;
import java.util.List;

public class AlterationConditionEditor extends InstanceChangingElementEditor<AlterationCondition> {

    public AlterationConditionEditor() {
        super(AlterationCondition.class, "alterationConditionType");
        LayoutUtils.fill(this, LayoutUtils.createVerticalBox(getTypeChooser(), getSpecificPartPanel()));
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected List<ClassEntry> getClassEntries(Object params) {
        List<ClassEntry> entries = new ArrayList<>();

        entries.add(new ClassEntry(EntityAroundCountAlterationCondition.class, () -> {
            EnumChooser<TargetType> targetTypeEditor = new EnumChooser<>(TargetType.class);
            ElementSetEditor<Creature> creatureFilterEditor = new ElementSetEditor(Creature.class);
            FloatInput distanceEditor = new FloatInput();
            EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
            IntegerInput countInput = new IntegerInput();
            bind(targetTypeEditor, "targetType", EntityAroundCountAlterationCondition.class);
            bind(creatureFilterEditor, "creatureFilter", EntityAroundCountAlterationCondition.class);
            bind(distanceEditor, "distance", EntityAroundCountAlterationCondition.class);
            bind(operatorChooser, "operator", EntityAroundCountAlterationCondition.class);
            bind(countInput, "count", EntityAroundCountAlterationCondition.class);
            return LayoutUtils.createVerticalBox(
                    LayoutUtils.createHorizontalLabelledBox("generic.searchDistance", distanceEditor, "generic.target", targetTypeEditor),
                    creatureFilterEditor,
                    LayoutUtils.createHorizontalBox(LayoutUtils.label("generic.sum"), operatorChooser, countInput)
            );
        }));

        entries.add(new ClassEntry(StructureAroundCountAlterationCondition.class, () -> {
            ElementSetEditor<Structure> structureFilterEditor = new ElementSetEditor(Structure.class);
            FloatInput distanceEditor = new FloatInput();
            EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
            IntegerInput countInput = new IntegerInput();
            bind(structureFilterEditor, "structureFilter", StructureAroundCountAlterationCondition.class);
            bind(distanceEditor, "distance", StructureAroundCountAlterationCondition.class);
            bind(operatorChooser, "operator", StructureAroundCountAlterationCondition.class);
            bind(countInput, "count", StructureAroundCountAlterationCondition.class);
            return LayoutUtils.createVerticalBox(
                    structureFilterEditor,
                    LayoutUtils.createHorizontalBox(LayoutUtils.labelled("generic.searchDistance", distanceEditor), LayoutUtils.label("generic.sum"), operatorChooser, countInput)
            );
        }));

        entries.add(new ClassEntry(InventoryContainsAlterationCondition.class, () -> {
            ElementSetEditor<Item> itemsEditor = new ElementSetEditor(Item.class);
            EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
            IntegerInput valueInput = new IntegerInput();
            bind(itemsEditor, "items", InventoryContainsAlterationCondition.class);
            bind(operatorChooser, "operator", InventoryContainsAlterationCondition.class);
            bind(valueInput, "value", InventoryContainsAlterationCondition.class);
            return LayoutUtils.createVerticalBox(
                    itemsEditor,
                    LayoutUtils.createHorizontalBox(LayoutUtils.label("generic.sum"), operatorChooser, valueInput)
            );
        }));

        entries.add(new ClassEntry(StatAlterationCondition.class, () -> {
            EnumChooser<StatType> statTypeEditor = new EnumChooser<>(StatType.class);
            EnumChooser<FloatComparison> operatorEditor = new EnumChooser<>(FloatComparison.class);
            FloatInput valueInput = new FloatInput();
            bind(statTypeEditor, "statType", StatAlterationCondition.class);
            bind(operatorEditor, "operator", StatAlterationCondition.class);
            bind(valueInput, "value", StatAlterationCondition.class);
            return LayoutUtils.createHorizontalBox(statTypeEditor, operatorEditor, valueInput);
        }));

        entries.add(new ClassEntry(HealthAlterationCondition.class, () -> {
            EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
            PercentInput percentValueInput = new PercentInput();
            bind(operatorChooser, "operator", HealthAlterationCondition.class);
            bind(percentValueInput, "percentValue", HealthAlterationCondition.class);
            return LayoutUtils.createHorizontalBox(LayoutUtils.label("generic.health"), operatorChooser, percentValueInput);
        }));

        entries.add(new ClassEntry(ActualSpeedAlterationCondition.class, () -> {
            EnumChooser<FloatComparison> operatorChooser = new EnumChooser<>(FloatComparison.class);
            FloatInput valueInput = new FloatInput();
            bind(operatorChooser, "operator", ActualSpeedAlterationCondition.class);
            bind(valueInput, "value", ActualSpeedAlterationCondition.class);
            return LayoutUtils.createHorizontalBox(operatorChooser, valueInput);
        }));

        entries.add(new ClassEntry(TileAlterationCondition.class, () -> {
            ElementSetEditor<Tile> tilesEditor = new ElementSetEditor(Tile.class);
            bind(tilesEditor, "tiles", TileAlterationCondition.class);
            return tilesEditor;
        }));

        entries.add(new ClassEntry(RandomAlterationCondition.class, () -> {
            PercentInput percentChanceInput = new PercentInput();
            bind(percentChanceInput, "percentChance", RandomAlterationCondition.class);
            return LayoutUtils.single(LayoutUtils.labelled("generic.chance", percentChanceInput));
        }));

        return entries;
    }

    @Override
    protected void initialize(AlterationCondition oldInstance, AlterationCondition newInstance) {
    }

}
