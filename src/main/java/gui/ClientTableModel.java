package gui;

import model.Client;
import util.DialogManagerUtil;
import util.DBConnectionUtil;
import util.MethodsUtil;
import view.Clients;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;

public class ClientTableModel extends AbstractTableModel {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DNI = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_FIRSTSURNAME = 3;
    private static final int COLUMN_SECONDSURNAME = 4;
    private static final int COLUMN_BIRTHDATE = 5;
    private static final int COLUMN_PROFESSION = 6;
    private static final int COLUMN_ANNUAL_EXPENSE = 7;
    private static final int COLUMN_ANNUAL_SAVINGS = 8;
    private final String[] columnNames = {"Id #", "Dni", "Nombre", "Primer apellido", "Segundo apellido", "F.Nacimiento", "Profesion", "Gasto anual (€)", "Ahorro anual (€)"};
    private final Class[] collumnDataType = new Class[]{Integer.class, String.class, String.class, String.class, String.class, LocalDate.class, String.class, Double.class, Double.class};
    private ArrayList<Client> clients;
    private ArrayList<Double> clientAnnualExpenses;
    private ArrayList<Double> clientAnnualSavings;

    public ClientTableModel(ArrayList<Client> clients) {
        this.clients = clients;
        clientAnnualExpenses = new ArrayList<>();
        for (Client client : clients) {
            clientAnnualExpenses.add(DBConnectionUtil.dataBase.getClientAnnualExpense(client));
        }
        clientAnnualSavings = new ArrayList<>();
        for (Client client : clients) {
            clientAnnualSavings.add(Double.valueOf(MethodsUtil.formatDouble(((DBConnectionUtil.dataBase.getClientAnnualExpense(client) / 100) * DBConnectionUtil.dataBase.getClientAnnualDiscountPercentage(client)))));
        }
    }

    public void setData(ArrayList<Client> clients) {
        this.clients = clients;
        Clients.clientList = clients;
        clientAnnualExpenses = new ArrayList<>();
        for (Client client : clients) {
            clientAnnualExpenses.add(DBConnectionUtil.dataBase.getClientAnnualExpense(client));
        }
        clientAnnualSavings = new ArrayList<>();
        for (Client client : clients) {
            clientAnnualSavings.add(Double.valueOf(MethodsUtil.formatDouble(((DBConnectionUtil.dataBase.getClientAnnualExpense(client) / 100) * DBConnectionUtil.dataBase.getClientAnnualDiscountPercentage(client)))));

        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return clients.size();
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
        Client client = clients.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_ID -> returnValue = client.getId();
            case COLUMN_DNI -> returnValue = client.getDni();
            case COLUMN_NAME -> returnValue = client.getName();
            case COLUMN_FIRSTSURNAME -> returnValue = client.getFirstSurname();
            case COLUMN_SECONDSURNAME -> returnValue = client.getSecondSurname();
            case COLUMN_BIRTHDATE -> returnValue = client.getBirthDate();
            case COLUMN_PROFESSION -> returnValue = client.getProfession();
            case COLUMN_ANNUAL_EXPENSE -> returnValue = clientAnnualExpenses.get(rowIndex);
            case COLUMN_ANNUAL_SAVINGS -> returnValue = clientAnnualSavings.get(rowIndex);
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Client getClient(int rowIndex) {
        return clients.get(rowIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Client client = clients.get(rowIndex);
        if (value != null) {
            if (columnIndex == COLUMN_DNI) {
                client.setDni((String) value);
            } else if (columnIndex == COLUMN_NAME) {
                client.setName((String) value);
            } else if (columnIndex == COLUMN_FIRSTSURNAME) {
                client.setFirstSurname((String) value);
            } else if (columnIndex == COLUMN_SECONDSURNAME) {
                client.setSecondSurname((String) value);
            } else if (columnIndex == COLUMN_BIRTHDATE) {
                client.setBirthDate(LocalDate.parse(String.valueOf(value)));
            } else if (columnIndex == COLUMN_PROFESSION) {
                client.setProfession((String) value);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
        }
    }

    public void removeRow(int row) {
        clients.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex != COLUMN_ID) && (columnIndex != COLUMN_ANNUAL_SAVINGS) && (columnIndex != COLUMN_ANNUAL_EXPENSE);
    }
}