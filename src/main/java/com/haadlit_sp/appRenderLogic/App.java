package com.haadlit_sp.appRenderLogic;

import com.haadlit_sp.appCoreLogic.AppInfo;
import com.haadlit_sp.appCoreLogic.Net;
import com.haadlit_sp.appCoreLogic.settings.AppSettings;
import com.haadlit_sp.appCoreLogic.store.SettingsStore;
import com.haadlit_sp.appRenderLogic.pages.HomePage;
import com.haadlit_sp.appRenderLogic.pages.SessionsPage;
import com.haadlit_sp.appRenderLogic.pages.SettingsPage;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Top-level window. Owns the single {@link Net} core shared by the pages,
 * persists user settings, and switches between the Home dashboard, the
 * Sessions browser and the Settings page via a {@link CardLayout}.
 */
public final class App {

    public static final String HOME = "Home";
    public static final String SESSIONS = "Sessions";
    public static final String SETTINGS = "Settings";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JFrame frame = new JFrame(AppInfo.NAME);

    private final SettingsStore settingsStore = new SettingsStore();
    private AppSettings settings;

    private final SessionsPage sessionsPage;

    public App() {
        Net core = new Net();
        settings = settingsStore.load();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(settings.alwaysOnTop());

        cardPanel.setBackground(Theme.BG);

        HomePage homePage = new HomePage(this, core);
        sessionsPage = new SessionsPage(this, core);
        SettingsPage settingsPage = new SettingsPage(this);
        cardPanel.add(homePage, HOME);
        cardPanel.add(sessionsPage, SESSIONS);
        cardPanel.add(settingsPage, SETTINGS);

        showPage(HOME);
        frame.setContentPane(cardPanel);
        frame.setVisible(true);
    }

    /** Switches pages, refreshing the sessions list each time it is shown. */
    public void showPage(String pageName) {
        if (SESSIONS.equals(pageName)) {
            sessionsPage.refresh();
        }
        cardLayout.show(cardPanel, pageName);
    }

    public boolean isAlwaysOnTop() {
        return settings.alwaysOnTop();
    }

    /** Applies the always-on-top preference live and persists it. */
    public void setAlwaysOnTop(boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
        settings = new AppSettings(alwaysOnTop);
        settingsStore.save(settings);
    }
}
