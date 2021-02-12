package com.pixurvival.core.message;

import com.pixurvival.core.interactionDialog.InteractionDialog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInteractionDialog {

	/**
	 * May be null to close interaction dialog
	 */
	private InteractionDialog dialog;
}
