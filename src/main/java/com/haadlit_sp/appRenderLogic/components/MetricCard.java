package com.haadlit_sp.appRenderLogic.components;

import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * One dashboard tile: a dim uppercase label above a bold 22px value.
 */
public final class MetricCard extends JPanel {

    private final JLabel valueLabel;

    public MetricCard(String label, Color valueColor) {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(label.toUpperCase());
        titleLabel.setForeground(Theme.DIM);
        titleLabel.setFont(Theme.label());

        valueLabel = new JLabel("--");
        valueLabel.setForeground(valueColor);
        valueLabel.setFont(Theme.value());

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
