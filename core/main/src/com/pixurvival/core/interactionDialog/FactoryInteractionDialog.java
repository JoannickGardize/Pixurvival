package com.pixurvival.core.interactionDialog;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.FactoryStructure;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.PlayerInventoryInteractions;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.WorldKryo;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FactoryInteractionDialog extends InteractionDialog implements InventoryListener {

	private FactoryStructure factoryStructure;

	private Inventory recipesInventory;
	private Inventory fuelsInventory;
	private Inventory resultsInventory;

	private @Getter @Setter float fuelTank;
	private @Getter @Setter int actualCraftIndex = -1;
	private @Getter @Setter long finishTime;

	public FactoryInteractionDialog(InteractionDialogHolder owner, FactoryStructure factoryStructure) {
		super(owner);
		this.factoryStructure = factoryStructure;
		recipesInventory = new Inventory(factoryStructure.getRecipeSize());
		fuelsInventory = new Inventory(factoryStructure.getFuelSize());
		resultsInventory = new Inventory(factoryStructure.getResultSize());
	}

	@Override
	public void interact(PlayerEntity player, int index, boolean splitMode) {
		ItemStack heldItemStack = player.getInventory().getHeldItemStack();
		if (index < recipesInventory.size()) {
			if (heldItemStack == null || factoryStructure.getPossibleRecipes().contains(heldItemStack.getItem())) {
				PlayerInventoryInteractions.interact(player.getInventory(), recipesInventory, index, splitMode);
			}
		} else if (index < recipesInventory.size() + fuelsInventory.size()) {
			if (heldItemStack == null || factoryStructure.getPossibleFuels().contains(heldItemStack.getItem())) {
				PlayerInventoryInteractions.interact(player.getInventory(), fuelsInventory, index - recipesInventory.size(), splitMode);
			}
		} else {
			if (heldItemStack == null) {
				PlayerInventoryInteractions.interact(player.getInventory(), resultsInventory, index - recipesInventory.size() - fuelsInventory.size(), splitMode);
			}
		}
	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		notifyChanged();
	}

	@Override
	public void set(InteractionDialog other) {
		FactoryInteractionDialog dialog = (FactoryInteractionDialog) other;
		recipesInventory.set(dialog.recipesInventory);
		fuelsInventory.set(dialog.fuelsInventory);
		resultsInventory.set(dialog.resultsInventory);
		fuelTank = dialog.getFuelTank();
		actualCraftIndex = dialog.getActualCraftIndex();
		finishTime = dialog.getFinishTime();
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<FactoryInteractionDialog> {

		@Override
		public void write(Kryo kryo, Output output, FactoryInteractionDialog object) {
			ByteBuffer buffer = ByteBufferUtils.asByteBuffer(output);
			World world = ((WorldKryo) kryo).getWorld();
			VarLenNumberIO.writePositiveVarInt(buffer, object.factoryStructure.getId());
			TeamMemberSerialization.write(buffer, object.getOwner(), false);
			FactoryInteractionDialog.write(buffer, object, world);
			output.setPosition(buffer.position());
		}

		@Override
		public FactoryInteractionDialog read(Kryo kryo, Input input, Class<FactoryInteractionDialog> type) {
			World world = ((WorldKryo) kryo).getWorld();
			ByteBuffer buffer = ByteBufferUtils.asByteBuffer(input);
			FactoryStructure factoryStructure = (FactoryStructure) world.getContentPack().getStructures().get(VarLenNumberIO.readPositiveVarInt(buffer));
			TeamMember owner = TeamMemberSerialization.read(buffer, world, false);
			FactoryInteractionDialog result = new FactoryInteractionDialog(owner instanceof InteractionDialogHolder ? (InteractionDialogHolder) owner : null, factoryStructure);
			FactoryInteractionDialog.read(buffer, result, world);
			input.setPosition(buffer.position());
			return result;
		}

	}

	public static void write(ByteBuffer buffer, FactoryInteractionDialog dialog, World world) {
		dialog.recipesInventory.write(buffer);
		dialog.fuelsInventory.write(buffer);
		dialog.resultsInventory.write(buffer);
		buffer.putFloat(dialog.fuelTank);
		VarLenNumberIO.writeVarInt(buffer, dialog.actualCraftIndex);
		ByteBufferUtils.writeTime(buffer, world, dialog.finishTime);
	}

	public static void read(ByteBuffer buffer, FactoryInteractionDialog dialog, World world) {
		dialog.getRecipesInventory().apply(world, buffer);
		dialog.getFuelsInventory().apply(world, buffer);
		dialog.getResultsInventory().apply(world, buffer);
		dialog.setFuelTank(buffer.getFloat());
		dialog.setActualCraftIndex(VarLenNumberIO.readVarInt(buffer));
		dialog.setFinishTime(ByteBufferUtils.readTime(buffer, world));
	}

}
