package com.pixurvival.server.ui;

import lombok.NonNull;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

public class TextPaneOutputStream extends OutputStream {

    private JTextPane textPane;
    private StringBuilder sb = new StringBuilder();

    private AttributeSet aset;

    public TextPaneOutputStream(@NonNull JTextPane textPane, @NonNull Color appendColor) {
        this.textPane = textPane;
        StyleContext sc = StyleContext.getDefaultStyleContext();
        aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, appendColor);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
    }

    @Override
    public void write(int b) throws IOException {
        sb.append((char) b);
    }

    @Override
    @SneakyThrows
    public void flush() throws IOException {
        textPane.getDocument().insertString(textPane.getDocument().getLength(), sb.toString(), aset);
        sb.setLength(0);
    }

}
