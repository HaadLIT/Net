package com.haadlit_sp.appRenderLogic.components;

import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * App header: title + subtitle on the left, a live status dot on the right.
 */
public final class Header extends JPanel {

    private final JLabel status = new JLabel();

    public Header() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(4, 2, 16, 2));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Net Usage Tracker");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.title());

        JLabel subtitle = new JLabel("Live network data usage per session");
        subtitle.setForeground(Theme.DIM);
        subtitle.setFont(Theme.subtitle());

        titles.add(title);
        titles.add(subtitle);

        status.setFont(Theme.label());
        setActive(false);

        add(titles, BorderLayout.WEST);
        add(status, BorderLayout.EAST);
    }

    public void setActive(boolean active) {
        status.setText(active ? "● Session active" : "● No active session");
        status.setForeground(active ? Theme.MINT : Theme.DIM);
    }
}
