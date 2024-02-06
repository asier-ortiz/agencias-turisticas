package view;

import javax.swing.*;

import com.github.lgooddatepicker.components.TimePicker;
import model.Employee;
import model.Tour;
import util.DBConnectionUtil;
import util.DialogManagerUtil;

import java.awt.*;
import java.time.LocalDateTime;

public class NewTour extends JFrame {
    private JPanel newClientWindow;
    private JComboBox<Employee> employeeComboBox;
    private JTextField titleTextField;
    private JTextArea descriptionJtextArea;
    private JComboBox<String> topicComboBox;
    private JTextField maxAttendeesTextField;
    private JButton clearButton;
    private JButton saveButton;
    private JTextField startingPointTextField;
    private JTextField priceTextField;
    private JLabel numberOfCharactersLabel;
    private com.github.lgooddatepicker.components.DatePicker startingDatePicker;
    private TimePicker startingTimePicker;
    private JTextField placeTextField;
    private int characterCounter;

    public JPanel getNewTourWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        topicComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"ACTIVIDAD_FAMILIAR", "CONCIERTO",
                "DANZA_TEATRO", "DEPORTES", "EVENTOS_GASTRONOMICOS", "EXPOSICIONES", "FESTIVALES", "FIESTAS_TRADICIONES", "CONGRESOS_FERIAS", "RUTAS", "OTROS"}));
        descriptionJtextArea.setLineWrap(true);
        descriptionJtextArea.setWrapStyleWord(true);
        DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true).stream().sorted().forEach(e -> employeeComboBox.addItem(e));
        return newClientWindow;
    }

    public NewTour() {
        saveButton.addActionListener(e -> {
            if (!validateForm()) {
                DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
            } else {
                Tour Tour = new Tour(
                        employeeComboBox.getItemAt(employeeComboBox.getSelectedIndex()),
                        titleTextField.getText().trim(),
                        descriptionJtextArea.getText().trim(),
                        topicComboBox.getItemAt(topicComboBox.getSelectedIndex()),
                        placeTextField.getText().trim(),
                        startingPointTextField.getText().trim(),
                        Integer.parseInt(maxAttendeesTextField.getText().trim()),
                        Double.parseDouble(priceTextField.getText().trim()),
                        LocalDateTime.of(startingDatePicker.getDate(), startingTimePicker.getTime()));
                if (DBConnectionUtil.dataBase.insertTour(Tour)) {
                    clearInputsData();
                    Tours.tourTableModel.fireTableRowsInserted(Tours.tourTableModel.getRowCount(), Tours.tourTableModel.getRowCount());
                }
            }
        });

        clearButton.addActionListener(e -> clearInputsData());

        descriptionJtextArea.addCaretListener(e -> {
            numberOfCharactersLabel.setForeground(Color.BLACK);
            if (descriptionJtextArea.getText().length() > 50) {
                numberOfCharactersLabel.setForeground(Color.RED);
            }
            if (descriptionJtextArea.getText().equals("")) {
                characterCounter = 0;
                numberOfCharactersLabel.setText(characterCounter + " / " + "50");
            } else {
                characterCounter = descriptionJtextArea.getAccessibleContext().getAccessibleText().getCharCount();
                numberOfCharactersLabel.setText(characterCounter + " / " + "50");
            }
        });
    }

    private boolean validateForm() {
        if (titleTextField.getText().trim().isEmpty()) {
            return false;
        } else if (descriptionJtextArea.getText().trim().isEmpty()) {
            return false;
        } else if (placeTextField.getText().trim().isEmpty()) {
            return false;
        } else if (startingPointTextField.getText().trim().isEmpty()) {
            return false;
        } else if (maxAttendeesTextField.getText().trim().isEmpty()) {
            return false;
        } else if (!maxAttendeesTextField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(maxAttendeesTextField.getText().trim());
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (priceTextField.getText().trim().isEmpty()) {
            return false;
        } else if (!priceTextField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(priceTextField.getText().trim());
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (startingDatePicker.getDate() == null) {
            return false;
        } else return startingTimePicker.getTime() == null;
        return true;
    }

    public void clearInputsData() {
        employeeComboBox.setSelectedIndex(0);
        titleTextField.setText("");
        descriptionJtextArea.setText("");
        topicComboBox.setSelectedIndex(0);
        placeTextField.setText("");
        startingPointTextField.setText("");
        maxAttendeesTextField.setText("");
        priceTextField.setText("");
        startingDatePicker.setDate(null);
        startingTimePicker.setTime(null);
    }
}