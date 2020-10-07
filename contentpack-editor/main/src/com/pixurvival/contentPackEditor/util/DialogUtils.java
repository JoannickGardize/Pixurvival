package com.pixurvival.contentPackEditor.util;

import javax.swing.JOptionPane;

import com.pixurvival.contentPackEditor.TranslationService;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DialogUtils {

	public static void showErrorDialog(String messageKey) {
		JOptionPane.showMessageDialog(null, TranslationService.getInstance().getString(messageKey), TranslationService.getInstance().getString("dialog.errorTitle"), JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorDialog(Throwable e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), TranslationService.getInstance().getString("dialog.errorTitle"), JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorDialog(String messageKey, Throwable e) {
		JOptionPane.showMessageDialog(null, TranslationService.getInstance().getString(messageKey) + ": " + e.getMessage(), TranslationService.getInstance().getString("dialog.errorTitle"),
				JOptionPane.ERROR_MESSAGE);
	}

	public static void showMessageDialog(String messageKey) {
		JOptionPane.showMessageDialog(null, TranslationService.getInstance().getString(messageKey), "", JOptionPane.INFORMATION_MESSAGE);
	}
}
