package gui;

import util.DBConnectionUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TableListener implements TableModelListener {
    private final JTable table;

    public TableListener(JTable table) {
        this.table = table;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        TableModel model = (TableModel) e.getSource();
        switch (table.getName()) {
            case "employeesTable":
                if (e.getType() == TableModelEvent.UPDATE) {
                    EmployeeTableModel employeeTableModel = ((EmployeeTableModel) model);
                    DBConnectionUtil.dataBase.updateEmployee(employeeTableModel.getEmployee(e.getFirstRow()));
                } else if (e.getType() == TableModelEvent.INSERT) {
                    EmployeeTableModel employeeTableModel = ((EmployeeTableModel) model);
                    employeeTableModel.setData(DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true));
                }
                break;
            case "clientsTable":
                if (e.getType() == TableModelEvent.UPDATE) {
                    ClientTableModel clientTableModel = ((ClientTableModel) model);
                    DBConnectionUtil.dataBase.updateClient(clientTableModel.getClient(e.getFirstRow()));
                } else if (e.getType() == TableModelEvent.INSERT) {
                    ClientTableModel clientTableModel = ((ClientTableModel) model);
                    clientTableModel.setData(DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true));
                }
                break;
            case "toursTable":
                if (e.getType() == TableModelEvent.UPDATE) {
                    TourTableModel tourTableModel = ((TourTableModel) model);
                    DBConnectionUtil.dataBase.updateTour(tourTableModel.getTour(e.getFirstRow()));
                    table.clearSelection();
                } else if (e.getType() == TableModelEvent.INSERT) {
                    TourTableModel tourTableModel = ((TourTableModel) model);
                    tourTableModel.setData(DBConnectionUtil.dataBase.getAllActiveTours());
                }
                break;
        }
    }
}