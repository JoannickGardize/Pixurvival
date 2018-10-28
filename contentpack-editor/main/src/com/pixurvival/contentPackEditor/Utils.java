package com.pixurvival.contentPackEditor;

import javax.swing.JOptionPane;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

	public static void showErrorDialog(String messageKey) {
		JOptionPane.showMessageDialog(null, TranslationService.getInstance().getString(messageKey),
				TranslationService.getInstance().getString("dialog.errorTitle"), JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorDialog(Throwable e) {
		JOptionPane.showMessageDialog(null, e.getMessage(),
				TranslationService.getInstance().getString("dialog.errorTitle"), JOptionPane.ERROR_MESSAGE);
	}

	public static boolean isValidFilePath(String path) {
		return path.matches("^([-_.A-Za-z0-9]+\\/)*[-_.A-Za-z0-9]+$");
	}
}
