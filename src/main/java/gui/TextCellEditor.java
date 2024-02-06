package gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TextCellEditor extends DefaultCellEditor {
    private final JTextField jTextField;

    public TextCellEditor(JTextField jTextField) {
        super(jTextField);
        this.jTextField = new JTextField();
    }

    @Override
    public Object getCellEditorValue() {
        if (jTextField.getText().trim().isEmpty()) {
            return null;
        }
        return jTextField.getText().trim();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        jTextField.setFont(new Font("Verdana", Font.BOLD, 16));
        jTextField.setBorder(new LineBorder(Color.BLACK));
        jTextField.setHorizontalAlignment(JLabel.CENTER);
        jTextField.setBorder(new LineBorder(Color.GREEN, 2));
        if (table.getName().equals("toursTable") && (column == 7)) {
            jTextField.setText(String.valueOf(value));
        } else if (table.getName().equals("toursTable") && (column == 9)) {
            jTextField.setText(String.valueOf(value));
        } else {
            jTextField.setText((String) value);
        }
        return jTextField;
    }
}