package view;

import javax.swing.*;

import com.github.lgooddatepicker.components.DatePicker;
import model.Client;
import util.DBConnectionUtil;
import util.DialogManagerUtil;

public class NewClient extends JFrame {
    private JPanel newClientWindow;
    private JTextField dniTextField;
    private JTextField nameTextField;
    private JTextField firstSurnameTextField;
    private JTextField secondSurnameTextField;
    private DatePicker birthDatePicker;
    private JTextField professionTextField;
    private JButton clearButton;
    private JButton saveButton;

    public JPanel getNewClientWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return newClientWindow;
    }

    public NewClient() {
        saveButton.addActionListener(e -> {
            if (!validateForm()) {
                DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
            } else {
                Client client = new Client(
                        dniTextField.getText().trim(),
                        nameTextField.getText().trim(),
                        firstSurnameTextField.getText().trim(),
                        secondSurnameTextField.getText().trim(),
                        birthDatePicker.getDate(),
                        professionTextField.getText().trim());
                if (DBConnectionUtil.dataBase.insertClient(client)) {
                    clearInputsData();
                    Clients.clientTableModel.fireTableRowsInserted(Clients.clientTableModel.getRowCount(), Clients.clientTableModel.getRowCount());
                }
            }
        });
        clearButton.addActionListener(e -> clearInputsData());
    }

    private boolean validateForm() {
        if (dniTextField.getText().trim().isEmpty()) {
            return false;
        } else if (nameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (firstSurnameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (secondSurnameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (birthDatePicker.getDate() == null) {
            return false;
        } else return !professionTextField.getText().trim().isEmpty();
    }

    private void clearInputsData() {
        dniTextField.setText("");
        nameTextField.setText("");
        firstSurnameTextField.setText("");
        secondSurnameTextField.setText("");
        birthDatePicker.setDate(null);
        professionTextField.setText("");
    }
}