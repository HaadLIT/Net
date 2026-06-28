package com.haadlit_sp.appRenderLogic.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Central dark-theme palette and fonts. Keeping these in one place means the
 * whole UI restyles from a single file.
 */
public final class Theme {

    public static final Color BG = Color.decode("#0F111A");          // app background
    public static final Color CARD = Color.decode("#181C2C");        // metric card panel
    public static final Color CARD_BORDER = Color.decode("#262B40"); // subtle card outline
    public static final Color ACCENT = Color.decode("#52B7FF");      // electric blue primary
    public static final Color MINT = Color.decode("#78FFB4");        // GB/hour value
    public static final Color DANGER = Color.decode("#FF5C7A");      // stop button / errors
    public static final Color TEXT = Color.decode("#E6E9F0");        // primary text
    public static final Color DIM = Color.decode("#8A90A6");         // labels / inactive

    private static final String FAMILY = "SansSerif";

    private Theme() {
    }

    public static Font title() {
        return new Font(FAMILY, Font.BOLD, 20);
    }

    public static Font subtitle() {
        return new Font(FAMILY, Font.PLAIN, 12);
    }

    public static Font label() {
        return new Font(FAMILY, Font.PLAIN, 12);
    }

    public static Font value() {
        return new Font(FAMILY, Font.BOLD, 22);
    }

    public static Font mono() {
        return new Font(Font.MONOSPACED, Font.PLAIN, 13);
    }
}
