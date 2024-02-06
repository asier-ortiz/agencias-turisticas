package gui;

import util.MethodsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableCellRenderer extends DefaultTableCellRenderer {
    private final Font cellFont = new Font("Verdana", Font.PLAIN, 12);
    private final Border greenEmphasisBorder = new LineBorder(Color.GREEN, 2);
    private final Border yellowEmphasisBorder = new LineBorder(Color.YELLOW, 2);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setForeground(Color.WHITE);
        cell.setFont(cellFont);
        setHorizontalAlignment(CENTER);
        if (!isSelected) {
            cell.setBackground(row % 2 == 0 ? Color.DARK_GRAY : Color.GRAY);
        }
        if (hasFocus) {
            setBorder(greenEmphasisBorder);
        }
        if (value instanceof JButton) {
            JButton button = (JButton) value;
            button.setEnabled(false);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(true);
            if (hasFocus) {
                button.setBorder(yellowEmphasisBorder);
            }
            switch (button.getName()) {
                case "one_star":
                    ImageIcon icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\one_star.png"), 150, 100);
                    button.setIcon(icon);
                    break;
                case "two_stars":
                    icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\two_stars.png"), 150, 100);
                    button.setIcon(icon);
                    break;
                case "three_stars":
                    icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\three_stars.png"), 150, 100);
                    button.setIcon(icon);
                    break;
                case "four_stars":
                    icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\four_stars.png"), 150, 100);
                    button.setIcon(icon);
                    break;
                case "five_stars":
                    icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\five_stars.png"), 150, 100);
                    button.setIcon(icon);
                    break;
            }
            return button;
        }
        if (value instanceof JScrollPane) {
            JScrollPane jScrollPane = (JScrollPane) value;
            jScrollPane.setBorder(BorderFactory.createEmptyBorder());
            JTextArea jTextArea = (JTextArea) ((JScrollPane) value).getViewport().getView();
            jTextArea.setForeground(Color.WHITE);
            jTextArea.setFont(cellFont);
            jTextArea.setLineWrap(true);
            if (!isSelected) {
                jTextArea.setBackground(row % 2 == 0 ? Color.DARK_GRAY : Color.GRAY);
            } else {
                jTextArea.setBackground(Color.BLACK);
            }
            if (hasFocus) {
                jScrollPane.setBorder(greenEmphasisBorder);
            }
            return jScrollPane;
        }
        return cell;
    }
}