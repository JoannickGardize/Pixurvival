package com.pixurvival.contentPackEditor.component.util;

import com.pixurvival.contentPackEditor.ContentPackEditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.NoninvertibleTransformException;

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
            GraphicsConfiguration[] configs = screen.getConfigurations();
            Point screenSize = new Point(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
            if (configs.length > 0) {
                try {
                    configs[0].getDefaultTransform().inverseTransform(screenSize, screenSize);
                } catch (NoninvertibleTransformException e) {
                    e.printStackTrace();
                }
            }
            if (x + getWidth() > screenSize.x) {
                x = screenSize.x - getWidth();
            }
            if (y + getHeight() > screenSize.y) {
                y = screenSize.y - getHeight();
            }
        }
        setLocation(x, y);
        setVisible(true);
    }

    protected void onClose() {

    }
}
