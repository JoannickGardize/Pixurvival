package com.pixurvival.core.message;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.pixurvival.core.Direction;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.PlayerEndGameData;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.contentPack.summary.GameModeSummary;
import com.pixurvival.core.contentPack.summary.RoleSummary;
import com.pixurvival.core.interactionDialog.FactoryInteractionDialog;
import com.pixurvival.core.interactionDialog.InventoryInteractionDialog;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.AddStructureUpdate;
import com.pixurvival.core.map.chunk.update.DamageableStructureUpdate;
import com.pixurvival.core.map.chunk.update.FactoryStructureUpdate;
import com.pixurvival.core.map.chunk.update.HarvestableStructureUpdate;
import com.pixurvival.core.map.chunk.update.RemoveStructureUpdate;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.ChooseRoleRequest;
import com.pixurvival.core.message.lobby.ContentPackReady;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.LobbyServerMessage;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.core.message.lobby.RefuseContentPack;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;
import com.pixurvival.core.message.playerRequest.ChatRequest;
import com.pixurvival.core.message.playerRequest.CloseInteractionDialogRequest;
import com.pixurvival.core.message.playerRequest.CraftItemRequest;
import com.pixurvival.core.message.playerRequest.DialogInteractionActionRequest;
import com.pixurvival.core.message.playerRequest.DropItemRequest;
import com.pixurvival.core.message.playerRequest.EquipmentActionRequest;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.PlayerEquipmentAbilityRequest;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.core.system.mapLimits.MapLimitsAnchorRun;
import com.pixurvival.core.system.mapLimits.MapLimitsSystemData;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.core.util.Vector2;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KryoInitializer {

	public static void apply(Kryo kryo) {
		register(kryo, String[].class);
		register(kryo, long[].class);
		register(kryo, Direction.class);
		register(kryo, Vector2.class);
		register(kryo, PlayerMovementRequest.class);
		register(kryo, TimeSync.class);
		register(kryo, LoginRequest.class);
		register(kryo, LoginResponse.class);
		register(kryo, WorldUpdate.class);
		register(kryo, CreateWorld.class);
		register(kryo, byte[].class);
		register(kryo, Version.class);
		register(kryo, ContentPackIdentifier.class);
		register(kryo, ContentPackIdentifier[].class);
		register(kryo, StartGame.class);
		register(kryo, ContentPackPart.class);
		register(kryo, GameReady.class);
		register(kryo, ItemStack.class);
		register(kryo, Inventory.class);
		register(kryo, PlayerInventory.class);
		register(kryo, InventoryActionRequest.class);
		register(kryo, CompressedChunk.class);
		register(kryo, InteractStructureRequest.class);
		register(kryo, HarvestableStructureUpdate.class);
		register(kryo, AddStructureUpdate.class);
		register(kryo, RemoveStructureUpdate.class);
		register(kryo, CraftItemRequest.class);
		register(kryo, DropItemRequest.class);
		register(kryo, EquipmentActionRequest.class);
		register(kryo, PlaceStructureRequest.class);
		register(kryo, PlayerEquipmentAbilityRequest.class);
		register(kryo, ChatRequest.class);
		register(kryo, ChatEntry.class);
		register(kryo, UseItemRequest.class);
		register(kryo, PlayerInformation.class);
		register(kryo, PlayerInformation[].class);
		register(kryo, TeamComposition.class);
		register(kryo, TeamComposition[].class);
		register(kryo, Spectate.class);
		register(kryo, PlayerDead.class);
		register(kryo, PlayerDead[].class);
		register(kryo, PlayerEndGameData.class);
		register(kryo, PlayerEndGameData[].class);
		register(kryo, EndGameData.class);
		register(kryo, RefreshRequest.class);
		register(kryo, ClientStream.class);
		register(kryo, TimeSync.class);
		register(kryo, SoundEffect.class);
		register(kryo, ChangeTeamRequest.class);
		register(kryo, CreateTeamRequest.class);
		register(kryo, EnterLobby.class);
		register(kryo, LobbyData.class);
		register(kryo, LobbyPlayer.class);
		register(kryo, LobbyPlayer[].class);
		register(kryo, LobbyTeam.class);
		register(kryo, LobbyTeam[].class);
		register(kryo, ReadyRequest.class);
		register(kryo, RemoveTeamRequest.class);
		register(kryo, RenameTeamRequest.class);
		register(kryo, Locale.class);
		register(kryo, Locale[].class);
		register(kryo, ChooseGameModeRequest.class);
		register(kryo, ContentPackCheck.class);
		register(kryo, ContentPackReady.class);
		register(kryo, RefuseContentPack.class);
		register(kryo, ContentPackRequest.class);
		register(kryo, DamageableStructureUpdate.class);
		register(kryo, IntegerInterval.class);
		register(kryo, ContentPackSummary.class);
		register(kryo, ContentPackSummary[].class);
		kryo.register(HashMap.class, new MapSerializer());
		kryo.register(LinkedHashMap.class, new MapSerializer());
		register(kryo, RoleSummary.class);
		register(kryo, RoleSummary[].class);
		register(kryo, GameModeSummary.class);
		register(kryo, GameModeSummary[].class);
		register(kryo, ChooseRoleRequest.class);
		register(kryo, LobbyServerMessage.class);
		register(kryo, int[].class);
		register(kryo, ItemCraftAvailable.class);
		register(kryo, Respawn.class);
		register(kryo, PlayerRespawn.class);
		register(kryo, PlayerRespawn[].class);
		register(kryo, Rectangle.class);
		register(kryo, MapLimitsAnchorRun.class);
		register(kryo, MapLimitsSystemData.class);
		register(kryo, InventoryInteractionDialog.class);
		register(kryo, UpdateInteractionDialog.class);
		register(kryo, DialogInteractionActionRequest.class);
		register(kryo, CloseInteractionDialogRequest.class);
		register(kryo, FactoryInteractionDialog.class);
		register(kryo, FactoryStructureUpdate.class);
	}

	@SuppressWarnings("unchecked")
	private static void register(Kryo kryo, Class<?> clazz) {
		try {
			Class<?>[] internalClasses = clazz.getClasses();
			for (Class<?> internalClass : internalClasses) {
				if (internalClass.getSimpleName().equals("Serializer") && internalClass.getSuperclass() == Serializer.class) {
					kryo.register(clazz, ((Class<? extends Serializer<?>>) internalClass).newInstance());
					return;
				}
			}
			kryo.register(clazz);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
