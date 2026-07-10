package com.haadlit_sp.appRenderLogic.pages;

import com.haadlit_sp.appCoreLogic.Net;
import com.haadlit_sp.appCoreLogic.session.Metrics;
import com.haadlit_sp.appCoreLogic.session.Session;
import com.haadlit_sp.appCoreLogic.util.Format;
import com.haadlit_sp.appRenderLogic.App;
import com.haadlit_sp.appRenderLogic.components.Buttons;
import com.haadlit_sp.appRenderLogic.components.Header;
import com.haadlit_sp.appRenderLogic.components.MetricCard;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Locale;

/**
 * Home dashboard: header status, a 2x2 grid of live metric cards and the
 * Start / Check Now / Stop controls. A single 1000ms {@link Timer} drives all
 * live updates and fires on the EDT, so every mutation here is EDT-safe.
 */
public final class HomePage extends JPanel {

    private static final System.Logger LOG = System.getLogger(HomePage.class.getName());

    private final Net core;

    private final Header header = new Header();
    private final MetricCard dataCard = new MetricCard("Data Used", Theme.ACCENT);
    private final MetricCard durationCard = new MetricCard("Session Duration", Theme.TEXT);
    private final MetricCard rateCard = new MetricCard("Estimated GB / Hour", Theme.MINT);
    private final MetricCard speedCard = new MetricCard("Live Speed", Theme.ACCENT);

    private final JButton startButton = Buttons.filled("Start Session", Theme.ACCENT);
    private final JButton stopButton = Buttons.outlined("Stop Session", Theme.DANGER);
    private final JLabel footer = new JLabel(" ");

    private final Timer timer = new Timer(1000, e -> update());

    public HomePage(App app, Net core) {
        this.core = core;

        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        add(header, BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildSouth(app), BorderLayout.SOUTH);

        wireButtons();
        setSessionState(false);
    }

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(dataCard);
        grid.add(durationCard);
        grid.add(rateCard);
        grid.add(speedCard);
        return grid;
    }

    private JPanel buildSouth(App app) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(startButton);
        actions.add(stopButton);

        JButton sessionsButton = Buttons.outlined("Sessions »", Theme.DIM);
        sessionsButton.addActionListener(e -> app.showPage(App.SESSIONS));
        JButton settingsButton = Buttons.outlined("Settings", Theme.DIM);
        settingsButton.addActionListener(e -> app.showPage(App.SETTINGS));
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);
        nav.add(settingsButton);
        nav.add(sessionsButton);

        JPanel buttonRow = new JPanel(new BorderLayout());
        buttonRow.setOpaque(false);
        buttonRow.add(actions, BorderLayout.WEST);
        buttonRow.add(nav, BorderLayout.EAST);

        footer.setForeground(Theme.DIM);
        footer.setFont(Theme.label());
        footer.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttonRow, BorderLayout.NORTH);
        south.add(footer, BorderLayout.SOUTH);
        return south;
    }

    private void wireButtons() {
        startButton.addActionListener(e -> onStart());
        stopButton.addActionListener(e -> onStop());
    }

    private void onStart() {
        try {
            core.start();
            clearError();
            setSessionState(true);
            update();
            timer.start();
        } catch (Exception ex) {
            showError("Could not start session: " + ex.getMessage());
            LOG.log(System.Logger.Level.ERROR, "Failed to start session", ex);
        }
    }

    private void onStop() {
        timer.stop();
        try {
            Session saved = core.stop();
            showInfo("Saved session #" + String.format("%04d", saved.number()));
        } catch (Exception ex) {
            showError("Could not save session: " + ex.getMessage());
            LOG.log(System.Logger.Level.ERROR, "Failed to stop/save session", ex);
        }
        setSessionState(false);
    }

    /** Reads fresh metrics and repaints the cards. Safe to call any time. */
    private void update() {
        if (!core.isActive()) {
            return;
        }
        try {
            Metrics m = core.refresh();
            dataCard.setValue(Format.bytes(m.dataUsedBytes()));
            durationCard.setValue(Format.duration(m.durationMillis()));
            rateCard.setValue(String.format(Locale.ROOT, "%.2f GB/h", m.gbPerHour()));
            speedCard.setValue(String.format(Locale.ROOT, "%.1f KB/s", m.speedKBs()));
        } catch (Exception ex) {
            showError("Read failed: " + ex.getMessage());
            LOG.log(System.Logger.Level.WARNING, "Metric refresh failed", ex);
        }
    }

    private void setSessionState(boolean active) {
        header.setActive(active);
        startButton.setEnabled(!active);
        stopButton.setEnabled(active);
    }

    private void showError(String message) {
        footer.setForeground(Theme.DANGER);
        footer.setText(message);
    }

    private void showInfo(String message) {
        footer.setForeground(Theme.DIM);
        footer.setText(message);
    }

    private void clearError() {
        footer.setText(" ");
    }
}
