package com.haadlit_sp.appRenderLogic.components;

import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;

/**
 * Factory for the three button styles used across the app: filled (primary),
 * outlined (secondary) and danger-tinted (stop).
 */
public final class Buttons {

    private Buttons() {
    }

    /** Solid accent fill with dark text — the primary action. */
    public static JButton filled(String text, Color fill) {
        JButton button = base(text);
        button.setBackground(fill);
        button.setForeground(Theme.BG);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return button;
    }

    /** Transparent fill with a coloured outline — secondary actions. */
    public static JButton outlined(String text, Color color) {
        JButton button = base(text);
        button.setBackground(Theme.CARD);
        button.setForeground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(9, 17, 9, 17)));
        return button;
    }

    private static JButton base(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.label());
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
