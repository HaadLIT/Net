package com.haadlit_sp.appRenderLogic;

import com.haadlit_sp.appCoreLogic.Net;
import com.haadlit_sp.appRenderLogic.pages.HomePage;
import com.haadlit_sp.appRenderLogic.pages.SessionsPage;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Top-level window. Owns the single {@link Net} core shared by both pages and
 * switches between the Home dashboard and the Sessions browser via a
 * {@link CardLayout}.
 */
public final class App {

    public static final String HOME = "Home";
    public static final String SESSIONS = "Sessions";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final SessionsPage sessionsPage;

    public App() {
        Net core = new Net();

        JFrame frame = new JFrame("Net Usage Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 460);
        frame.setLocationRelativeTo(null);

        cardPanel.setBackground(Theme.BG);

        HomePage homePage = new HomePage(this, core);
        sessionsPage = new SessionsPage(this, core);
        cardPanel.add(homePage, HOME);
        cardPanel.add(sessionsPage, SESSIONS);

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
}
