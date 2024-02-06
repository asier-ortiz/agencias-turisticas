package gui;

import util.DialogManagerUtil;
import model.Employee;
import util.MethodsUtil;
import view.Employees;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeTableModel extends AbstractTableModel {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DNI = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_FIRSTNAME = 3;
    private static final int COLUMN_BIRTHDATE = 4;
    private static final int COLUMN_NATIONALITY = 5;
    private static final int COLUMN_EMAIL = 6;
    private static final int COLUMN_ROLE = 7;
    private final String[] columnNames = {"Id #", "Dni", "Nombre", "Apellido", "F.Nacimiento", "Nacionalidad", "Email", "Rol"};
    private final Class[] collumnDataType = new Class[]{Integer.class, String.class, String.class, String.class, LocalDate.class, String.class, String.class, String.class};
    private ArrayList<Employee> employeeList;

    public EmployeeTableModel(ArrayList<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void setData(ArrayList<Employee> listEmployees) {
        this.employeeList = listEmployees;
        Employees.employeeList = listEmployees;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return employeeList.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return collumnDataType[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee employee = employeeList.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_ID -> returnValue = employee.getId();
            case COLUMN_DNI -> returnValue = employee.getDni();
            case COLUMN_NAME -> returnValue = employee.getName();
            case COLUMN_FIRSTNAME -> returnValue = employee.getFirstSurname();
            case COLUMN_BIRTHDATE -> returnValue = employee.getBirthDate();
            case COLUMN_NATIONALITY -> returnValue = employee.getNationality();
            case COLUMN_EMAIL -> returnValue = employee.getEmail();
            case COLUMN_ROLE -> returnValue = employee.getRole();
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Employee getEmployee(int rowIndex) {
        return employeeList.get(rowIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Employee employee = employeeList.get(rowIndex);
        if (value != null) {
            if (columnIndex == COLUMN_DNI) {
                employee.setDni((String) value);
            } else if (columnIndex == COLUMN_NAME) {
                employee.setName((String) value);
            } else if (columnIndex == COLUMN_FIRSTNAME) {
                employee.setFirstSurname((String) value);
            } else if (columnIndex == COLUMN_BIRTHDATE) {
                employee.setBirthDate(LocalDate.parse(String.valueOf(value)));
            } else if (columnIndex == COLUMN_NATIONALITY) {
                employee.setNationality((String) value);
            } else if (columnIndex == COLUMN_EMAIL) {
                employee.setEmail((String) value);
            } else if (columnIndex == COLUMN_ROLE) {
                employee.setRole((String) value);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
        }
    }

    public void removeRow(int row) {
        employeeList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != COLUMN_ID;
    }
}