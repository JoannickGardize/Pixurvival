package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request for content pack. Client -> Server : Request missing content pack to
 * be sent. Server -> Client : Inform the client the required content pack for
 * the game.
 * 
 * @author Joannick Gardize
 *
 */
@Getter
@NoArgsConstructor
public class RequestContentPacks extends ContentPackIdentifier {

	private static final long serialVersionUID = 1L;

}
