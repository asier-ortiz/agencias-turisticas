package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class ComboBoxCellEditor extends DefaultCellEditor {
    protected DefaultComboBoxModel<String> defaultTableRolComboBoxModel = new DefaultComboBoxModel<>(new String[]{"ADMIN", "EMPLEADO"});
    protected DefaultComboBoxModel<String> defaultTourTopicComboBoxModel = new DefaultComboBoxModel<>(new String[]{"ACTIVIDAD_FAMILIAR", "CONCIERTO",
            "DANZA_TEATRO", "DEPORTES", "EVENTOS_GASTRONOMICOS", "EXPOSICIONES", "FESTIVALES", "FIESTAS_TRADICIONES", "CONGRESOS_FERIAS", "RUTAS", "OTROS"});
    private final String option;

    public ComboBoxCellEditor(String option) {
        super(new JComboBox<String>());
        this.option = option;
    }

    private JComboBox<String> getComboBox() {
        return (JComboBox) editorComponent;
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
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        getComboBox().setRenderer(new DefaultListCellRenderer() {
            @Override
            public void paint(Graphics g) {
                setFont(new Font("Verdana", Font.BOLD, 16));
                setHorizontalAlignment(JLabel.CENTER);
                super.paint(g);
            }
        });

        switch (option) {
            case "TableRolOption" -> getComboBox().setModel(defaultTableRolComboBoxModel);
            case "TableTopicOption" -> getComboBox().setModel(defaultTourTopicComboBoxModel);
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}