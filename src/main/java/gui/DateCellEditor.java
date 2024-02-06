package gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.tableeditors.DateTimeTableEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.EventObject;

public class DateCellEditor extends DateTimeTableEditor {
    private final DatePicker datePicker;

    public DateCellEditor() {
        datePicker = new DatePicker();
    }

    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            int clickCount;
            clickCount = 2;
            return ((MouseEvent) evt).getClickCount() >= clickCount;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        if (datePicker.getDate() != null) {
            return datePicker.getDate().toString();
        }
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        datePicker.setDate((LocalDate) value);
        return datePicker;
    }
}