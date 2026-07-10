package com.haadlit_sp.appRenderLogic.components;

import com.haadlit_sp.appCoreLogic.io.AppConnection;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Live left-hand panel listing apps with active network connections during a
 * session, ranked most to least. Windows exposes no per-app byte counter, so
 * this shows connection counts, not data — labelled accordingly.
 */
public final class AppUsagePanel extends JPanel {

    private final DefaultListModel<AppConnection> model = new DefaultListModel<>();
    private final JList<AppConnection> list = new JList<>(model);
    private final JLabel hint = new JLabel("No active session");

    public AppUsagePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(Theme.CARD);
        setPreferredSize(new Dimension(190, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel title = new JLabel("ACTIVE APPS (connections)");
        title.setForeground(Theme.DIM);
        title.setFont(Theme.label());

        hint.setForeground(Theme.DIM);
        hint.setFont(Theme.label());

        list.setBackground(Theme.CARD);
        list.setForeground(Theme.TEXT);
        list.setFont(Theme.mono());
        list.setCellRenderer((jList, value, index, selected, focused) -> row(value));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.CARD);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);
    }

    /** Replaces the list with the current ranking; empty clears it. */
    public void setApps(List<AppConnection> apps) {
        model.clear();
        for (AppConnection app : apps) {
            model.addElement(app);
        }
        hint.setText(apps.isEmpty() ? "No apps on the network" : " ");
    }

    /** Empties the panel back to its idle state (session stopped). */
    public void clear() {
        model.clear();
        hint.setText("No active session");
    }

    private JPanel row(AppConnection app) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setBackground(Theme.CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel name = new JLabel(app.name());
        name.setForeground(Theme.TEXT);
        name.setFont(Theme.mono());

        JLabel count = new JLabel(String.valueOf(app.connections()));
        count.setForeground(Theme.ACCENT);
        count.setFont(Theme.mono());

        panel.add(name, BorderLayout.CENTER);
        panel.add(count, BorderLayout.EAST);
        return panel;
    }
}
