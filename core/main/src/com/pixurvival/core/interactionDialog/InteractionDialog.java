package com.pixurvival.core.interactionDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Abstract class for player interaction dialogs (e.g. chest's inventory, NPC's
 * dialogs).
 * 
 * @author SharkHendrix
 *
 */
@NoArgsConstructor
public abstract class InteractionDialog {

	/**
	 * owner of this dialog (Chest, NPC...)
	 */
	private @Getter InteractionDialogHolder owner;

	/**
	 * Players actually viewing this dialog
	 */
	private @Getter Collection<PlayerEntity> viewers = new ArrayList<>();

	/**
	 * Listeners to get notified of content changes of this dialog
	 */
	private Collection<InteractionDialogChangeListener> listeners = new ArrayList<>();

	protected InteractionDialog(InteractionDialogHolder owner) {
		this.owner = owner;
	}

	public void addViewer(PlayerEntity player) {
		viewers.add(player);
	}

	public void removeViewer(PlayerEntity player) {
		viewers.remove(player);
	}

	public void foreachViewers(Consumer<PlayerEntity> action) {
		viewers.forEach(action);
	}

	public int viewerCount() {
		return viewers.size();
	}

	public void addListener(InteractionDialogChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(InteractionDialogChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Interact with the dialog. {@code splitMode} may not be used if the
	 * {@code index} does not represents an item stack slot for the implementation.
	 * 
	 * @param player
	 * @param index
	 * @param splitMode
	 */
	public abstract void interact(PlayerEntity player, int index, boolean splitMode);

	public void set(InteractionDialog other) {
		// Nothing by default
	}

	public void notifyChanged() {
		listeners.forEach(l -> l.changed(this));
	}
}
