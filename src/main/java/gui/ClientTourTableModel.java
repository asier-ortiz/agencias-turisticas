package gui;

import model.Client;
import view.Tours;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ClientTourTableModel extends AbstractTableModel {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DNI = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_FIRSTSURNAME = 3;
    private static final int COLUMN_SECONDSURNAME = 4;
    private final String[] columnNames = {"Id #", "Dni", "Nombre", "Primer apellido", "Segundo apellido"};
    private final Class[] collumnDataType = new Class[]{Integer.class, String.class, String.class, String.class, String.class};
    private ArrayList<Client> clients;

    public ClientTourTableModel(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void setData(ArrayList<Client> clients) {
        this.clients = clients;
        Tours.attendeesList = clients;
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
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Client getClient(int rowIndex) {
        return clients.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}