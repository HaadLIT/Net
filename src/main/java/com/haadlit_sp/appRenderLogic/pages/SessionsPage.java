package com.haadlit_sp.appRenderLogic.pages;

import com.haadlit_sp.appCoreLogic.Net;
import com.haadlit_sp.appCoreLogic.session.Session;
import com.haadlit_sp.appCoreLogic.util.Format;
import com.haadlit_sp.appRenderLogic.App;
import com.haadlit_sp.appRenderLogic.components.Buttons;
import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Past-sessions browser: a scrollable list on the left, the selected session's
 * details in the centre, and a top bar to look a session up by number.
 *
 * <p>The list refreshes automatically whenever the page becomes visible (via
 * {@link App#showPage}).
 */
public final class SessionsPage extends JPanel {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Net core;

    private final DefaultListModel<Session> model = new DefaultListModel<>();
    private final JList<Session> list = new JList<>(model);
    private final JTextArea details = new JTextArea();
    private final JTextField numberField = new JTextField(6);
    private final JLabel status = new JLabel(" ");

    public SessionsPage(App app, Net core) {
        this.core = core;

        setBackground(Theme.BG);
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        add(buildTopBar(app), BorderLayout.NORTH);
        add(buildList(), BorderLayout.WEST);
        add(buildDetails(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar(App app) {
        JButton homeButton = Buttons.outlined("« Home", Theme.DIM);
        homeButton.addActionListener(e -> app.showPage(App.HOME));

        JLabel prompt = new JLabel("Open session #");
        prompt.setForeground(Theme.DIM);
        prompt.setFont(Theme.label());

        numberField.setBackground(Theme.CARD);
        numberField.setForeground(Theme.TEXT);
        numberField.setCaretColor(Theme.TEXT);
        numberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JButton openButton = Buttons.filled("Open", Theme.ACCENT);
        openButton.addActionListener(e -> openByNumber());
        numberField.addActionListener(e -> openByNumber());

        JButton refreshButton = Buttons.outlined("Refresh", Theme.ACCENT);
        refreshButton.addActionListener(e -> refresh());

        JPanel lookup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        lookup.setOpaque(false);
        lookup.add(prompt);
        lookup.add(numberField);
        lookup.add(openButton);
        lookup.add(refreshButton);

        status.setForeground(Theme.DANGER);
        status.setFont(Theme.label());

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.add(homeButton, BorderLayout.WEST);
        bar.add(lookup, BorderLayout.CENTER);
        bar.add(status, BorderLayout.SOUTH);
        return bar;
    }

    private JScrollPane buildList() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(Theme.CARD);
        list.setForeground(Theme.TEXT);
        list.setSelectionBackground(Theme.ACCENT);
        list.setSelectionForeground(Theme.BG);
        list.setFont(Theme.mono());
        list.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        list.setCellRenderer((jList, value, index, selected, focused) -> {
            JLabel cell = new JLabel(rowLabel(value));
            cell.setOpaque(true);
            cell.setFont(Theme.mono());
            cell.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            cell.setBackground(selected ? Theme.ACCENT : Theme.CARD);
            cell.setForeground(selected ? Theme.BG : Theme.TEXT);
            return cell;
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                showSession(list.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(220, 0));
        scroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER));
        return scroll;
    }

    private JScrollPane buildDetails() {
        details.setEditable(false);
        details.setBackground(Theme.CARD);
        details.setForeground(Theme.TEXT);
        details.setFont(Theme.mono());
        details.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        details.setText("Select a session from the list, or look one up by number.");

        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER));
        return scroll;
    }

    /** Reloads the list from disk. Called by {@link App} when shown. */
    public void refresh() {
        status.setText(" ");
        model.clear();
        List<Session> sessions = core.history();
        for (Session session : sessions) {
            model.addElement(session);
        }
        if (model.isEmpty()) {
            details.setText("No sessions recorded yet. Start one from the Home page.");
        }
    }

    private void openByNumber() {
        String input = numberField.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            status.setText("Enter a valid session number.");
            return;
        }
        core.find(number).ifPresentOrElse(
                session -> {
                    status.setText(" ");
                    selectInList(number);
                    showSession(session);
                },
                () -> status.setText("Session #" + number + " not found."));
    }

    private void selectInList(int number) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).number() == number) {
                list.setSelectedIndex(i);
                list.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    private void showSession(Session session) {
        details.setText(formatSession(session));
        details.setCaretPosition(0);
    }

    private static String rowLabel(Session session) {
        return String.format("#%04d  ·  %s", session.number(), session.start().format(DATE));
    }

    private static String formatSession(Session session) {
        LocalDateTime start = session.start();
        LocalDateTime stop = session.stop();
        String duration = stop == null ? "--" : Format.duration(Duration.between(start, stop).toMillis());
        return new StringBuilder()
                .append("Session #").append(String.format("%04d", session.number())).append('\n')
                .append("Date:       ").append(start.format(DATE)).append('\n')
                .append("Started:    ").append(start.format(TIME)).append('\n')
                .append("Stopped:    ").append(stop == null ? "--" : stop.format(TIME)).append('\n')
                .append("Duration:   ").append(duration).append('\n')
                .append("Data Used:  ").append(Format.bytes(session.dataUsedBytes()))
                .append("  (").append(session.dataUsedBytes()).append(" bytes)")
                .toString();
    }
}
