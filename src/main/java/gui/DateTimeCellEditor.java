package gui;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.tableeditors.DateTimeTableEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EventObject;

public class DateTimeCellEditor extends DateTimeTableEditor {
    private final DateTimePicker dateTimePicker;

    public DateTimeCellEditor() {
        dateTimePicker = new DateTimePicker();
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
        if (dateTimePicker.getDatePicker().getDate() != null && dateTimePicker.getTimePicker().getTime() != null) {
            return LocalDateTime.of(dateTimePicker.getDatePicker().getDate(), dateTimePicker.getTimePicker().getTime());
        }
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        dateTimePicker.getDatePicker().setDate(LocalDate.from((LocalDateTime) value));
        dateTimePicker.getTimePicker().setTime(LocalTime.from((LocalDateTime) value));
        return dateTimePicker;
    }
}