package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JLabel;
import javax.swing.TransferHandler;

public class FrameTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	public static final DataFlavor SUPPORTED_DATA_FLAVOR = DataFlavor.stringFlavor;

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		return support.isDataFlavorSupported(SUPPORTED_DATA_FLAVOR);
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		boolean accept = false;
		if (canImport(support)) {
			try {
				Transferable t = support.getTransferable();
				Object value = t.getTransferData(SUPPORTED_DATA_FLAVOR);
				if (value instanceof String) {
					Component component = support.getComponent();
					if (component instanceof JLabel) {
						((JLabel) component).setText(value.toString());
						accept = true;
					}
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return accept;
	}
}
