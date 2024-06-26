package com.pixurvival.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.interactionDialog.InteractionDialogHolder;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.chunk.ChunkManagerPlugin;
import com.pixurvival.core.message.*;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyServerMessage;
import com.pixurvival.core.system.mapLimits.MapLimitsSystemData;
import com.pixurvival.core.team.TeamMember;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

class NetworkMessageHandler extends Listener {

    private Map<Class<?>, Consumer<?>> messageActions = new IdentityHashMap<>();

    private List<Object> receivedObjects = new ArrayList<>();

    public NetworkMessageHandler(PixurvivalClient game, Supplier<Collection<ChunkManagerPlugin>> chunkManagerPluginSupplier) {
        putMessageAction(LoginResponse.class, r -> game.notify(l -> l.loginResponse(r)));
        putMessageAction(CreateWorld.class, c -> game.createClientWorld(c, chunkManagerPluginSupplier.get()));
        putMessageAction(ContentPackPart.class, p -> game.getContentPackDownloadManager().accept(p));

        // TODO Download system
        // messageActions.put(RequestContentPacks.class, r -> {
        // game.checkMissingPacks(((RequestContentPacks) r).getIdentifiers());
        // });
        putMessageAction(TimeSync.class, o -> {
            TimeSync t = o;
            game.synchronizeTime(t);
        });
        putMessageAction(PlayerInventory.class, i -> {
            if (game.getMyInventory() != null) {
                game.getMyInventory().set(i);
            }
        });
        putMessageAction(WorldUpdate.class, game::offer);
        putMessageAction(StartGame.class, g -> {
            game.addPlugin(new WorldUpdater());
            game.getWorld().getTime().setTimeMillis(g.getWorldTime());
            game.notify(ClientGameListener::gameStarted);
        });
        putMessageAction(ChatEntry.class, c -> game.getWorld().getChatManager().received(c));
        putMessageAction(Spectate.class, game::spectate);
        putMessageAction(Respawn.class, game::respawn);
        putMessageAction(PlayerDead[].class, pd -> {
            for (PlayerDead playerDead : pd) {
                PlayerEntity player = game.getWorld().getPlayerEntities().get(playerDead.getId());
                player.getTeam().addDead(player);
                player.setRespawnTime(playerDead.getRespawnTime());
                game.getWorld().getEntityPool().notifyPlayerDied(player);
            }
        });
        putMessageAction(PlayerRespawn[].class, pr -> {
            for (PlayerRespawn playerRespawn : pr) {
                PlayerEntity player = game.getWorld().getPlayerEntities().get(playerRespawn.getId());
                player.getTeam().addAlive(player);
                player.setSpawnProtectionEndTime();
                game.getWorld().getEntityPool().notifyPlayerRespawned(player);
            }
        });
        putMessageAction(EndGameData.class, game::notifyGameEnded);
        putMessageAction(EnterLobby.class, e -> game.notify(ClientGameListener::enterLobby));
        putMessageAction(LobbyData.class, ll -> game.notify(l -> l.lobbyMessageReceived(ll)));
        putMessageAction(LobbyServerMessage.class, lm -> game.notify(l -> l.lobbyMessageReceived(lm)));
        putMessageAction(ContentPackCheck.class, game::checkContentPackValidity);
        putMessageAction(ItemCraftAvailable.class, i -> game.discovered(i.getItemCraftIds()));
        putMessageAction(MapLimitsSystemData.class, game::handleSystemData);
        putMessageAction(UpdateInteractionDialog.class, d -> {
            PlayerEntity p = game.getMyPlayer();
            if (p != null) {
                if (p.getInteractionDialog() != null && d.getDialog() != null && d.getDialog().getClass() == p.getInteractionDialog().getClass()
                        && d.getDialog().getOwner() == p.getInteractionDialog().getOwner()) {
                    p.getInteractionDialog().set(d.getDialog());
                } else {
                    p.setInteractionDialog(d.getDialog());
                }
                if (d.getDialog() != null) {
                    TeamMember owner = d.getDialog().getOwner();
                    if (owner != null) {
                        owner = owner.findIfNotFound();
                        if (owner instanceof InteractionDialogHolder) {
                            ((InteractionDialogHolder) owner).setInteractionDialog(p.getInteractionDialog());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void disconnected(Connection connection) {
    }

    @Override
    public void received(Connection connection, Object object) {
        synchronized (receivedObjects) {
            if (object != null) {
                receivedObjects.add(object);
            }
        }
    }

    public void consumeReceivedObjects() {
        synchronized (receivedObjects) {
            for (Object object : receivedObjects) {
                @SuppressWarnings("unchecked")
                Consumer<Object> action = (Consumer<Object>) messageActions.get(object.getClass());
                if (action != null) {
                    action.accept(object);
                } else {
                    Log.debug("Received unknown object type  : " + object.getClass().getSimpleName());
                }
            }
            receivedObjects.clear();
        }
    }

    private <T> void putMessageAction(Class<T> type, Consumer<T> action) {
        messageActions.put(type, action);
    }
}
