package gui;

import util.DialogManagerUtil;
import model.Tour;
import util.DBConnectionUtil;
import util.MethodsUtil;
import view.Tours;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TourTableModel extends AbstractTableModel {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_MANAGER = 1;
    private static final int COLUMN_TITLE = 2;
    private static final int COLUMN_DESCRIPTION = 3;
    private static final int COLUMN_TOPIC = 4;
    private static final int COLUMN_PLACE = 5;
    private static final int COLUMN_STARTING_POINT = 6;
    private static final int COLUMN_MAX_ATTENDEES = 7;
    private static final int COLUMN_START_DATE = 8;
    private static final int COLUMN_PRICE = 9;
    private static final int COLUMN_TOTAL_RAISED = 10;
    private final String[] columnNames = {"Id #", "Id Responsable #", "Titulo", "Descripcion", "Tema", "Lugar", "Punto de partida",
            "Máx. asistentes", "Fecha inicio", "Precio (€)", "Total Recaudado (€)"};
    private final Class[] collumnDataType = new Class[]{Integer.class, Integer.class, String.class, String.class, String.class,
            String.class, String.class, Integer.class, LocalDate.class, Double.class, Double.class};
    private ArrayList<Tour> tourList;
    private ArrayList<Double> tourTotalRaised;

    public TourTableModel(ArrayList<Tour> tourList) {
        this.tourList = tourList;
        tourTotalRaised = new ArrayList<>();
        for (Tour tour : tourList) {
            tourTotalRaised.add(Double.valueOf(MethodsUtil.formatDouble(DBConnectionUtil.dataBase.getTourTotalRaised(tour))));
        }
    }

    public void setData(ArrayList<Tour> tourList) {
        this.tourList = tourList;
        Tours.tourList = tourList;
        tourTotalRaised = new ArrayList<>();
        for (Tour tour : tourList) {
            tourTotalRaised.add(Double.valueOf(MethodsUtil.formatDouble(DBConnectionUtil.dataBase.getTourTotalRaised(tour))));
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return tourList.size();
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
        Tour tour = tourList.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_ID -> returnValue = tour.getId();
            case COLUMN_MANAGER -> returnValue = tour.getEmployee().getId();
            case COLUMN_TITLE -> returnValue = tour.getTitle();
            case COLUMN_DESCRIPTION -> returnValue = tour.getDescription();
            case COLUMN_TOPIC -> returnValue = tour.getTopic();
            case COLUMN_PLACE -> returnValue = tour.getPlace();
            case COLUMN_STARTING_POINT -> returnValue = tour.getStartingPoint();
            case COLUMN_MAX_ATTENDEES -> returnValue = tour.getMaxAttendees();
            case COLUMN_START_DATE -> returnValue = tour.getStartDate();
            case COLUMN_PRICE -> returnValue = tour.getPrice();
            case COLUMN_TOTAL_RAISED -> returnValue = tourTotalRaised.get(rowIndex);
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Tour getTour(int rowIndex) {
        return tourList.get(rowIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Tour tour = tourList.get(rowIndex);
        if (value != null) {
            if (columnIndex == COLUMN_TITLE) {
                tour.setTitle((String) value);
            } else if (columnIndex == COLUMN_DESCRIPTION) {
                tour.setDescription((String) value);
            } else if (columnIndex == COLUMN_TOPIC) {
                tour.setTopic((String) value);
            } else if (columnIndex == COLUMN_PLACE) {
                tour.setPlace((String) value);
            } else if (columnIndex == COLUMN_STARTING_POINT) {
                tour.setStartingPoint((String) value);
            } else if (columnIndex == COLUMN_MAX_ATTENDEES) {
                Integer integerValue;
                try {
                    integerValue = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    integerValue = null;
                }
                if (integerValue != null) {
                    tour.setMaxAttendees(Integer.parseInt((String) value));
                } else {
                    DialogManagerUtil.showErrorDialog("Error: Debes introducir un número");
                }
            } else if (columnIndex == COLUMN_START_DATE) {
                tour.setStartDate(LocalDateTime.parse(String.valueOf(value)));

            } else if (columnIndex == COLUMN_PRICE) {
                Double doubleValue;
                try {
                    doubleValue = Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    doubleValue = null;
                }
                if (doubleValue != null) {
                    tour.setPrice(Double.parseDouble((String) value));
                } else {
                    DialogManagerUtil.showErrorDialog("Error: Debes introducir un número");
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            DialogManagerUtil.showErrorDialog("Error: Por favor rellene todos los campos correctamente");
        }
    }

    public void removeRow(int row) {
        tourList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex != COLUMN_ID) && (columnIndex != COLUMN_MANAGER) && (columnIndex != COLUMN_TOTAL_RAISED);
    }
}