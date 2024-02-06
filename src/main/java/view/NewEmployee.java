package view;

import javax.swing.*;

import com.github.lgooddatepicker.components.DatePicker;
import model.Employee;
import util.DBConnectionUtil;
import util.DialogManagerUtil;

public class NewEmployee extends JFrame {
    private JPanel newEmployeeWindow;
    private JTextField dniTextField;
    private JTextField nameTextField;
    private JTextField firstSurnameTextField;
    private DatePicker birthDatePicker;
    private JTextField nationalotyTextField;
    private JComboBox<String> roleComboBox;
    private JTextField emailTextField;
    private JPasswordField passwordTextField;
    private JButton clearButton;
    private JButton saveButton;

    public JPanel getNewEmployeeWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"ADMIN", "EMPLEADO"}));
        return newEmployeeWindow;
    }

    public NewEmployee() {
        saveButton.addActionListener(e -> {
            if (!validateForm()) {
                DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
            } else {
                Employee employee = new Employee(
                        dniTextField.getText().trim(),
                        nameTextField.getText().trim(),
                        firstSurnameTextField.getText().trim(),
                        birthDatePicker.getDate(),
                        nationalotyTextField.getText().trim(),
                        roleComboBox.getItemAt(roleComboBox.getSelectedIndex()),
                        emailTextField.getText().trim(),
                        String.valueOf(passwordTextField.getPassword()));
                if (DBConnectionUtil.dataBase.insertEmployee(employee)) {
                    clearInputsData();
                    Employees.employeeTableModel.fireTableRowsInserted(Employees.employeeTableModel.getRowCount(), Employees.employeeTableModel.getRowCount());
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
        } else if (birthDatePicker.getDate() == null) {
            return false;
        } else if (nationalotyTextField.getText().trim().isEmpty()) {
            return false;
        } else return !emailTextField.getText().trim().isEmpty();
    }

    public void clearInputsData() {
        dniTextField.setText("");
        nameTextField.setText("");
        firstSurnameTextField.setText("");
        birthDatePicker.setDate(null);
        nationalotyTextField.setText("");
        roleComboBox.setSelectedIndex(0);
        emailTextField.setText("");
        passwordTextField.setText("");
    }
}