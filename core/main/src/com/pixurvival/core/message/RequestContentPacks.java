package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request for content pack. Client -> Server : Request missing content packs to
 * be sent. Server -> Client : Inform the client the required content pack list
 * for the game.
 * 
 * @author Joannick Gardize
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestContentPacks {
	private ContentPackIdentifier[] identifiers;
}
