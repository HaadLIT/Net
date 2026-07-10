package com.haadlit_sp.appRenderLogic.pages;

import com.haadlit_sp.appCoreLogic.AppInfo;
import com.haadlit_sp.appRenderLogic.App;
import com.haadlit_sp.appRenderLogic.components.Buttons;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * Third page: setting toggles plus the current version and change log.
 * Toggles call back into {@link App}, which applies and persists them.
 */
public final class SettingsPage extends JPanel {

    public SettingsPage(App app) {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        add(buildTopBar(app), BorderLayout.NORTH);
        add(buildBody(app), BorderLayout.CENTER);
    }

    private JPanel buildTopBar(App app) {
        JLabel title = new JLabel("Settings");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.title());

        JButton homeButton = Buttons.outlined("« Home", Theme.DIM);
        homeButton.addActionListener(e -> app.showPage(App.HOME));
        JButton sessionsButton = Buttons.outlined("Sessions »", Theme.DIM);
        sessionsButton.addActionListener(e -> app.showPage(App.SESSIONS));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);
        nav.add(homeButton);
        nav.add(sessionsButton);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.add(title, BorderLayout.WEST);
        bar.add(nav, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildBody(App app) {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(sectionLabel("PREFERENCES"));
        body.add(alwaysOnTopToggle(app));
        body.add(spacer());
        body.add(sectionLabel("ABOUT"));
        body.add(leftLabel("Version " + AppInfo.VERSION, Theme.ACCENT));
        body.add(spacer());
        body.add(sectionLabel("CHANGELOG"));
        body.add(changelog());
        return body;
    }

    private JCheckBox alwaysOnTopToggle(App app) {
        JCheckBox toggle = new JCheckBox("Always on top");
        toggle.setOpaque(false);
        toggle.setForeground(Theme.TEXT);
        toggle.setFont(Theme.label());
        toggle.setFocusPainted(false);
        toggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggle.setSelected(app.isAlwaysOnTop());
        toggle.addActionListener(e -> app.setAlwaysOnTop(toggle.isSelected()));
        return toggle;
    }

    private JScrollPane changelog() {
        JTextArea area = new JTextArea(AppInfo.CHANGELOG);
        area.setEditable(false);
        area.setBackground(Theme.CARD);
        area.setForeground(Theme.TEXT);
        area.setFont(Theme.mono());
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        return scroll;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.DIM);
        label.setFont(Theme.label());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    private JLabel leftLabel(String text, java.awt.Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(Theme.value());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private Component spacer() {
        return javax.swing.Box.createVerticalStrut(16);
    }
}
