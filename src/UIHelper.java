import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UIHelper provides utility methods for creating styled UI components.
 * Ensures consistent look across different operating systems (especially macOS).
 */
public class UIHelper {

    /**
     * Creates a styled button that works on all platforms including macOS.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // For macOS compatibility
        button.putClientProperty("JButton.buttonType", "square");

        // Add hover effect
        addHoverEffect(button, bgColor);

        return button;
    }

    /**
     * Creates a styled button with custom font size.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, int fontSize) {
        JButton button = createStyledButton(text, bgColor, fgColor);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        return button;
    }

    /**
     * Creates a styled button with preferred size.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, Dimension size) {
        JButton button = createStyledButton(text, bgColor, fgColor);
        if (size != null) {
            button.setPreferredSize(size);
        }
        return button;
    }

    /**
     * Creates a styled button with font size and preferred size.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, int fontSize, Dimension size) {
        JButton button = createStyledButton(text, bgColor, fgColor, fontSize);
        if (size != null) {
            button.setPreferredSize(size);
        }
        return button;
    }

    /**
     * Adds hover effect to a button - darkens on hover, lightens on press
     */
    public static void addHoverEffect(JButton button, Color baseColor) {
        Color hoverColor = darkenColor(baseColor, 0.15f);
        Color pressColor = darkenColor(baseColor, 0.25f);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(pressColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }
        });
    }

    /**
     * Darkens a color by a factor (0.0 - 1.0)
     */
    public static Color darkenColor(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    /**
     * Lightens a color by a factor (0.0 - 1.0)
     */
    public static Color lightenColor(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    /**
     * Styles an existing button for cross-platform compatibility.
     */
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "square");
        addHoverEffect(button, bgColor);
    }

    // Common colors
    public static final Color PRIMARY_BLUE = new Color(63, 81, 181);
    public static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    public static final Color DANGER_RED = new Color(211, 47, 47);
    public static final Color WARNING_ORANGE = new Color(255, 152, 0);
    public static final Color DARK_GREEN = new Color(46, 125, 50);
    public static final Color PURPLE = new Color(156, 39, 176);
    public static final Color CURRENT_QUESTION = new Color(33, 150, 243); // Bright blue for current question
    public static final Color ANSWERED_QUESTION = new Color(76, 175, 80); // Green for answered
    public static final Color UNANSWERED_QUESTION = new Color(200, 200, 200); // Gray for unanswered
}
