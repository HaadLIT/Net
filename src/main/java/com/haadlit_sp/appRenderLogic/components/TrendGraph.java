package com.haadlit_sp.appRenderLogic.components;

import com.haadlit_sp.appRenderLogic.theme.Theme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

/**
 * Lightweight line chart of a session's live-speed samples, drawn with plain
 * Java2D (no chart library). Shows the peaks and dips of throughput over the
 * session with a labelled Y-axis (0 at the bottom, the session's peak at the
 * top) and a faint filled area under the line.
 */
public final class TrendGraph extends JPanel {

    private static final int PAD_LEFT = 48;   // gutter for Y-axis labels
    private static final int PAD_RIGHT = 12;
    private static final int PAD_TOP = 24;    // room for the title
    private static final int PAD_BOTTOM = 14;
    private static final int[] GRID_PERCENT = {0, 25, 50, 75, 100};

    private List<Double> samples = List.of();

    public TrendGraph() {
        setBackground(Theme.CARD);
        setPreferredSize(new Dimension(0, 160));
        setMinimumSize(new Dimension(0, 90));
    }

    public void setSamples(List<Double> samples) {
        this.samples = (samples == null) ? List.of() : samples;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(Theme.label());
        g2.setColor(Theme.DIM);
        g2.drawString("LIVE SPEED TREND (KB/s)", PAD_LEFT, 16);

        if (samples.isEmpty()) {
            g2.drawString("No trend data for this session.", PAD_LEFT, getHeight() / 2);
            g2.dispose();
            return;
        }

        double max = 0;
        for (double value : samples) {
            max = Math.max(max, value);
        }
        if (max <= 0) {
            max = 1; // avoid divide-by-zero on a fully idle session
        }

        int plotWidth = getWidth() - PAD_LEFT - PAD_RIGHT;
        int plotHeight = getHeight() - PAD_TOP - PAD_BOTTOM;
        int baseY = PAD_TOP + plotHeight;

        drawAxis(g2, max, plotWidth, plotHeight, baseY);
        drawSeries(g2, max, plotWidth, plotHeight, baseY);

        g2.dispose();
    }

    private void drawAxis(Graphics2D g2, double max, int plotWidth, int plotHeight, int baseY) {
        FontMetrics fm = g2.getFontMetrics();
        for (int percent : GRID_PERCENT) {
            double fraction = percent / 100.0;
            int y = (int) Math.round(baseY - plotHeight * fraction);

            g2.setColor(Theme.CARD_BORDER);
            g2.drawLine(PAD_LEFT, y, PAD_LEFT + plotWidth, y);

            String label = axisLabel(max * fraction);
            g2.setColor(Theme.DIM);
            g2.drawString(label, PAD_LEFT - 6 - fm.stringWidth(label), y + fm.getAscent() / 2 - 1);
        }
    }

    private void drawSeries(Graphics2D g2, double max, int plotWidth, int plotHeight, int baseY) {
        int count = samples.size();
        Path2D line = new Path2D.Double();
        Path2D area = new Path2D.Double();
        for (int i = 0; i < count; i++) {
            double x = (count == 1) ? PAD_LEFT + plotWidth / 2.0
                    : PAD_LEFT + plotWidth * ((double) i / (count - 1));
            double y = PAD_TOP + plotHeight * (1 - samples.get(i) / max);
            if (i == 0) {
                line.moveTo(x, y);
                area.moveTo(x, baseY);
                area.lineTo(x, y);
            } else {
                line.lineTo(x, y);
                area.lineTo(x, y);
            }
        }
        double lastX = (count == 1) ? PAD_LEFT + plotWidth / 2.0 : PAD_LEFT + plotWidth;
        area.lineTo(lastX, baseY);
        area.closePath();

        g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 40));
        g2.fill(area);
        g2.setColor(Theme.ACCENT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(line);
    }

    private static String axisLabel(double kbs) {
        if (kbs <= 0) {
            return "0";
        }
        if (kbs < 10) {
            return String.format(Locale.ROOT, "%.1f", kbs);
        }
        return String.format(Locale.ROOT, "%.0f", kbs);
    }
}
