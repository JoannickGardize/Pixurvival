package com.pixurvival.core.system;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.FactoryCraft;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.system.interest.ChunkLoadInterest;
import com.pixurvival.core.system.interest.StructureChangeInterest;
import com.pixurvival.core.system.interest.WorldUpdateInterest;
import com.pixurvival.core.time.Time;
import com.pixurvival.core.util.MathUtils;

import lombok.Setter;

public class FactorySystem implements GameSystem, ChunkLoadInterest, StructureChangeInterest, WorldUpdateInterest {

	@Inject
	private @Setter Time time;

	private Set<Integer> factoryStructureTypes;

	private Map<ChunkPosition, Chunk> chunksWithFactories;

	@Override
	public boolean isRequired(World world) {
		if (!world.isServer()) {
			return false;
		}
		for (Structure structure : world.getContentPack().getStructures()) {
			if (structure instanceof FactoryStructure) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize(World world) {
		chunksWithFactories = new LinkedHashMap<>();
		factoryStructureTypes = new HashSet<>();
		for (Structure structure : world.getContentPack().getStructures()) {
			if (structure instanceof FactoryStructure) {
				factoryStructureTypes.add(structure.getId());
			}
		}
	}

	@Override
	public void update(float deltaTime) {
		for (Chunk chunk : chunksWithFactories.values()) {
			for (Entry<Integer, List<StructureEntity>> structures : chunk.getStructures()) {
				if (factoryStructureTypes.contains(structures.getKey())) {
					for (StructureEntity structureEntity : structures.getValue()) {
						update((FactoryStructureEntity) structureEntity);
					}
				}
			}
		}
	}

	/**
	 * Update the given factory.
	 * <ul>
	 * <li>First, check if the ongoing craft is finished, and put the result in the
	 * result inventory.
	 * <li>Second, check if there is no ongoing craft, if not, try to start a new
	 * one according the the available recipes and fuel. If the working flag is
	 * true, the previous finish time is taken as reference, making craft chaining
	 * time more precise and allow to catch up time when the chunk is reloaded, at
	 * the speed of one craft per tick.
	 * <li>Finally, the working flag is updated.
	 * </ul>
	 * For the first and second part, a flag is set to indicate that a change to the
	 * relevant inventories is expected, to avoid useless computation.
	 * 
	 * @param entity
	 */
	private void update(FactoryStructureEntity entity) {
		FactoryInteractionDialog dialog = entity.getInteractionDialog();
		if (!entity.isWaitingOutput() && dialog.getActualCraftIndex() >= 0 && time.getTimeMillis() >= dialog.getFinishTime()) {
			finishCraftIfPossible(entity, dialog);
		}
		if (!entity.isWaitingInput() && dialog.getActualCraftIndex() == -1) {
			startNewCraftIfPossible(entity, dialog);
		}
		entity.setWorking(dialog.getActualCraftIndex() != -1 && !entity.isWaitingOutput());
	}

	private void finishCraftIfPossible(FactoryStructureEntity entity, FactoryInteractionDialog dialog) {
		if (dialog.getResultsInventory().addAllOrFail(dialog.getFactoryStructure().getCrafts().get(dialog.getActualCraftIndex()).getResults())) {
			dialog.setActualCraftIndex(-1);
			dialog.notifyChanged();
		} else {
			entity.setWaitingOutput(true);
		}
	}

	private void startNewCraftIfPossible(FactoryStructureEntity entity, FactoryInteractionDialog dialog) {
		List<FactoryCraft> crafts = dialog.getFactoryStructure().getCrafts();
		float maxKnownCanRefuel = 0;
		for (int i = 0; i < crafts.size(); i++) {
			FactoryCraft craft = crafts.get(i);
			float fuelConsumption = craft.getFuelConsumption();
			if (fuelConsumption > maxKnownCanRefuel && canRefuel(dialog, fuelConsumption)) {
				maxKnownCanRefuel = fuelConsumption;
			}
			if (dialog.getRecipesInventory().contains(craft.getRecipes()) && fuelConsumption <= maxKnownCanRefuel) {
				dialog.getRecipesInventory().unsafeRemove(craft.getRecipes());
				refuel(dialog, fuelConsumption);
				dialog.setFuelTank(dialog.getFuelTank() - fuelConsumption);
				dialog.setActualCraftIndex(i);
				if (entity.isWorking()) {
					dialog.setFinishTime(dialog.getFinishTime() + craft.getDuration());
				} else {
					dialog.setFinishTime(time.getTimeMillis() + craft.getDuration());
				}
				dialog.notifyChanged();
				return;
			}
		}
		entity.setWaitingInput(true);
	}

	private boolean canRefuel(FactoryInteractionDialog dialog, float requiredAmount) {
		float totalFuelTank = dialog.getFuelTank();
		int fuelInventoryIndex = 0;
		Inventory fuelInventory = dialog.getFuelsInventory();
		while (totalFuelTank < requiredAmount) {
			fuelInventoryIndex = fuelInventory.findNotEmptySlot(fuelInventoryIndex);
			if (fuelInventoryIndex == -1) {
				return false;
			}
			ItemStack fuelSlot = fuelInventory.getSlot(fuelInventoryIndex);
			totalFuelTank += dialog.getFactoryStructure().getFuelAmounts().get(fuelSlot.getItem()) * fuelSlot.getQuantity();
			fuelInventoryIndex++;
		}
		return true;
	}

	private void refuel(FactoryInteractionDialog dialog, float requiredAmount) {
		float newFuelTank = dialog.getFuelTank();
		int fuelInventoryIndex = 0;
		Inventory fuelInventory = dialog.getFuelsInventory();
		while (newFuelTank < requiredAmount) {
			fuelInventoryIndex = fuelInventory.findNotEmptySlot(fuelInventoryIndex);
			if (fuelInventoryIndex == -1) {
				break;
			}
			ItemStack fuelSlot = fuelInventory.getSlot(fuelInventoryIndex);
			float fuelPerUnit = dialog.getFactoryStructure().getFuelAmounts().get(fuelSlot.getItem());
			int requiredQuantity = MathUtils.ceil((requiredAmount - newFuelTank) / fuelPerUnit);
			if (requiredQuantity >= fuelSlot.getQuantity()) {
				newFuelTank = fuelSlot.getQuantity() * fuelPerUnit;
				fuelInventory.setSlot(fuelInventoryIndex, null);
			} else {
				newFuelTank = requiredQuantity * fuelPerUnit;
				fuelInventory.setSlot(fuelInventoryIndex, fuelSlot.sub(requiredQuantity));
			}
			fuelInventoryIndex++;
		}
		dialog.setFuelTank(newFuelTank);
	}

	@Override
	public void chunkLoaded(Chunk chunk) {
		boolean chunkRequiresPut = true;
		for (Entry<Integer, List<StructureEntity>> structures : chunk.getStructures()) {
			if (factoryStructureTypes.contains(structures.getKey())) {
				chunk.setFactoryCount(chunk.getFactoryCount() + structures.getValue().size());
				if (chunkRequiresPut) {
					chunksWithFactories.put(chunk.getPosition(), chunk);
					chunkRequiresPut = false;
				}
			}
		}
	}

	@Override
	public void chunkUnloaded(Chunk chunk) {
		if (chunk.getFactoryCount() > 0) {
			chunksWithFactories.remove(chunk.getPosition());
		}
	}

	@Override
	public void structureChanged(StructureEntity structureEntity, StructureUpdate structureUpdate) {
		// Nothing to do
	}

	@Override
	public void structureAdded(StructureEntity structureEntity) {
		if (!(structureEntity instanceof FactoryStructureEntity)) {
			return;
		}
		Chunk chunk = structureEntity.getChunk();
		if (chunk.getFactoryCount() == 0) {
			chunksWithFactories.put(chunk.getPosition(), chunk);
		}
		chunk.setFactoryCount(chunk.getFactoryCount() + 1);
	}

	@Override
	public void structureRemoved(StructureEntity structureEntity) {
		if (!(structureEntity instanceof FactoryStructureEntity)) {
			return;
		}
		Chunk chunk = structureEntity.getChunk();
		if (chunk.getFactoryCount() == 1) {
			chunksWithFactories.remove(chunk.getPosition());
		}
		chunk.setFactoryCount(chunk.getFactoryCount() - 1);
	}
}
