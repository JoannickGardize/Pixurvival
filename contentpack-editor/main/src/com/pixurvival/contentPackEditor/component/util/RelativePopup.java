package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.pixurvival.contentPackEditor.ContentPackEditor;

public class RelativePopup extends JDialog {

	private static final long serialVersionUID = 1L;

	public RelativePopup() {
		super(JOptionPane.getRootFrame());
		setUndecorated(true);
		getRootPane().setBorder((Border) UIManager.get("PopupMenu.border"));
		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				onClose();
				setVisible(false);
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
			}
		});
	}

	public void show(Component relativeTo) {
		Point p = relativeTo.getLocationOnScreen();
		Frame rootFrame = ContentPackEditor.getInstance();
		int x = p.x;
		int y = p.y + relativeTo.getHeight();
		if (rootFrame.getGraphicsConfiguration() != null) {
			GraphicsDevice screen = rootFrame.getGraphicsConfiguration().getDevice();
			if (x + getWidth() > screen.getDisplayMode().getWidth()) {
				x = p.x + relativeTo.getWidth() - getWidth();
			}
			if (y + getHeight() > screen.getDisplayMode().getHeight()) {
				y = p.y - getHeight();
			}
		}
		setLocation(x, y);
		setVisible(true);
	}

	protected void onClose() {

	}
}
