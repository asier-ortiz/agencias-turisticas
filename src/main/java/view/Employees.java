package view;

import com.github.lgooddatepicker.components.DatePicker;
import gui.EmployeeTableModel;
import gui.TableListener;
import gui.TourTableModel;
import model.*;
import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.MethodsUtil;

import javax.swing.*;
import java.util.*;

import static util.MethodsUtil.*;

public class Employees extends JFrame {
    private JPanel employeesWindow;
    private JTable employeesTable;
    private JButton newEmployeeButton;
    private JPanel headerPanel;
    private JPanel employeesTitlePanel;
    private JPanel employeesTablePanel;
    private JLabel companyLogo;
    private JTabbedPane employeeTabbedPane;
    private JPanel employeeDetailsPanel;
    private JTable toursTable;
    private JList<String> sessionsJlist;
    private JList<String> historicJlist;
    private JPanel employeeDetailTitlePanel;
    private JButton reloadButton;
    private JLabel employeeDetailLabel;
    private JButton filterButton;
    private JPanel filterPanel;
    private JTextField idTextField;
    private JComboBox<String> roleComboBox;
    private JTextField dniTextField;
    private JTextField nameTextField;
    private JTextField surnameTextField;
    private DatePicker birthDatePicker;
    private JTextField nationalityTextField;
    private JButton searchButton;
    private JButton clearButton;
    private JCheckBox activeCheckBox;
    private JButton dismissReemployButton;
    private JLabel userRoleLabel;
    private Employee selectedEmplooye;
    public static ArrayList<Employee> employeeList;
    private ArrayList<Tour> tourList = new ArrayList<>();
    protected String[] searchFields = {"EMPLOYEE_ID", "DNI", "NAME", "FIRST_SURNAME", "BIRTH_DATE", "NATIONALITY", "ROLE", "ACTIVE"};
    public static EmployeeTableModel employeeTableModel;
    private TourTableModel tourTableModel;
    private TableListener employeesTableListener;
    private TableListener toursTableListener;

    public JPanel getEmployeesWindow() {
        userRoleLabel.setText(Login.connectedEmployee.getName() + " " + Login.connectedEmployee.getFirstSurname() + " - " + Login.connectedEmployee.getRole());
        dismissReemployButton.setEnabled(false);
        filterPanel.setVisible(false);
        roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"TODOS", "ADMIN", "EMPLEADO"}));
        employeeList = DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true);
        employeesTable.setName("employeesTable");
        employeeTableModel = new EmployeeTableModel(employeeList);
        employeesTable.setModel(employeeTableModel);
        employeesTableListener = new TableListener(employeesTable);
        employeesTable.getModel().addTableModelListener(employeesTableListener);
        setTableLookAndFeel(employeesTable, true);
        toursTable.setName("toursTable");
        tourTableModel = new TourTableModel(tourList);
        toursTable.setModel(tourTableModel);
        toursTableListener = new TableListener(toursTable);
        toursTable.getModel().addTableModelListener(toursTableListener);
        setTableLookAndFeel(toursTable, true);
        setButtonsIconsAndSize();
        return employeesWindow;
    }

    public Employees() {

        reloadButton.addActionListener(e -> reloadData(DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true)));

        dismissReemployButton.addActionListener(e -> {
            if (selectedEmplooye.isActive()) {
                if (selectedEmplooye.getId() == Login.connectedEmployee.getId()) {
                    DialogManagerUtil.showErrorDialog("Error: No puede darse de baja a sí mismo");
                } else {
                    if (DBConnectionUtil.dataBase.dismissEmployee(selectedEmplooye.getId())) {
                        employeeTableModel.removeRow(employeesTable.getSelectedRow());
                    }
                }
            } else {
                if (DBConnectionUtil.dataBase.reemployEmployee(selectedEmplooye.getId())) {
                    employeeTableModel.removeRow(employeesTable.getSelectedRow());
                }
            }
        });

        employeesTable.getSelectionModel().addListSelectionListener(event -> {
            if (employeesTable.getSelectedRow() > -1) {
                dismissReemployButton.setEnabled(true);
                selectedEmplooye = employeeList.get(employeesTable.getSelectedRow());
                employeeDetailLabel.setText("Detalle empleado #" + selectedEmplooye.getId());
                tourList = DBConnectionUtil.dataBase.getAllToursCreatedBy(selectedEmplooye);
                tourTableModel.setData(tourList);
                toursTable.setModel(tourTableModel);
                setTableLookAndFeel(toursTable, true);
                historicJlist.setModel(defaultListModelRegistrationCancelation(selectedEmplooye));
                sessionsJlist.setModel(defaultListModelClockInClockOut(selectedEmplooye));
            } else {
                dismissReemployButton.setEnabled(false);
                tourList = new ArrayList<>();
                tourTableModel.setData(tourList);
                toursTable.setModel(tourTableModel);
                setTableLookAndFeel(toursTable, true);
                historicJlist.setModel(new DefaultListModel<>());
                sessionsJlist.setModel(new DefaultListModel<>());
            }
        });

        newEmployeeButton.addActionListener(e -> {
            setTitle("Nuevo empleado");
            setContentPane(new NewEmployee().getNewEmployeeWindow());
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            pack();
            setVisible(true);
        });

        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

        searchButton.addActionListener(e -> {
            Integer id;
            try {
                id = Integer.parseInt(idTextField.getText().trim());
            } catch (NumberFormatException ex) {
                id = null;
            }
            List<String> searchFieldsValues = Arrays.asList(
                    id == null ? "" : String.valueOf(id),
                    dniTextField.getText().trim(),
                    nameTextField.getText().trim(),
                    surnameTextField.getText().trim(),
                    birthDatePicker.getDate() == null ? "" : formatDate(birthDatePicker.getDate()),
                    nationalityTextField.getText().trim(),
                    roleComboBox.getItemAt(roleComboBox.getSelectedIndex()),
                    activeCheckBox.isSelected() ? "1" : "0"
            );
            Map<String, String> searchFieldValueMap = new HashMap<>();
            for (int i = 0; i < searchFields.length; i++) {
                searchFieldValueMap.put(searchFields[i], searchFieldsValues.get(i));
            }
            ArrayList<Employee> result = DBConnectionUtil.dataBase.getFilteredEmployees(searchFieldValueMap);
            if (!result.isEmpty()) {
                reloadData(result);
            } else {
                DialogManagerUtil.showErrorDialog("Sin resultados...");
            }
        });

        clearButton.addActionListener(e -> clearInputsData());
    }

    public void clearInputsData() {
        idTextField.setText("");
        dniTextField.setText("");
        nameTextField.setText("");
        surnameTextField.setText("");
        birthDatePicker.setDate(null);
        nationalityTextField.setText("");
        roleComboBox.setSelectedIndex(0);
        activeCheckBox.setSelected(true);
        reloadData(DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true));
    }

    public void reloadData(ArrayList<Employee> employees) {
        employeeList = employees;
        employeeTableModel.setData(employeeList);
        employeesTable.setModel(employeeTableModel);
        employeesTable.getModel().addTableModelListener(employeesTableListener);
        setTableLookAndFeel(employeesTable, true);
        tourTableModel.setData(new ArrayList<>());
        toursTable.setModel(tourTableModel);
        toursTable.setModel(tourTableModel);
        toursTableListener = new TableListener(toursTable);
        toursTable.getModel().addTableModelListener(toursTableListener);
        setTableLookAndFeel(toursTable, true);
        historicJlist.setModel(new DefaultListModel<>());
        sessionsJlist.setModel(new DefaultListModel<>());

    }

    private void setButtonsIconsAndSize() {
        ImageIcon newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\new.png"), 35, 35);
        newEmployeeButton.setIcon(newIcon);
        newEmployeeButton.setOpaque(false);
        newEmployeeButton.setContentAreaFilled(false);
        newEmployeeButton.setBorderPainted(false);
        newEmployeeButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\filter.png"), 35, 35);
        filterButton.setIcon(newIcon);
        filterButton.setOpaque(false);
        filterButton.setContentAreaFilled(false);
        filterButton.setBorderPainted(false);
        filterButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\reload.png"), 35, 35);
        reloadButton.setIcon(newIcon);
        reloadButton.setOpaque(false);
        reloadButton.setContentAreaFilled(false);
        reloadButton.setBorderPainted(false);
        reloadButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\magnifyingglass.png"), 35, 35);
        searchButton.setIcon(newIcon);
        searchButton.setOpaque(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\broom.png"), 35, 35);
        clearButton.setIcon(newIcon);
        clearButton.setOpaque(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setBorderPainted(false);
        clearButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\switch.png"), 35, 35);
        dismissReemployButton.setIcon(newIcon);
        dismissReemployButton.setOpaque(false);
        dismissReemployButton.setContentAreaFilled(false);
        dismissReemployButton.setBorderPainted(false);
        dismissReemployButton.setFocusPainted(false);
        switch (Login.selectedCompany) {
            case 0 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_one.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 1 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_two.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 2 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_three.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 3 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_four.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
        }
    }

    private DefaultListModel<String> defaultListModelRegistrationCancelation(Employee employee) {
        DefaultListModel<String> defaultListModelRegistrationCancelation = new DefaultListModel<>();
        DBConnectionUtil.dataBase.getEmployeeRegistrationCancellation(employee).stream().sorted().forEach(defaultListModelRegistrationCancelation::addElement);
        return defaultListModelRegistrationCancelation;
    }

    private DefaultListModel<String> defaultListModelClockInClockOut(Employee employee) {
        DefaultListModel<String> defaultListModelClockInClockOut = new DefaultListModel<>();
        DBConnectionUtil.dataBase.getEmployeeClockInsClockOuts(employee).stream().sorted().forEach(defaultListModelClockInClockOut::addElement);
        return defaultListModelClockInClockOut;
    }
}