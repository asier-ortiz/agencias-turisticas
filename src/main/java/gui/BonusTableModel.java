package gui;

import model.Bonus;
import view.Clients;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;

public class BonusTableModel extends AbstractTableModel {
    private static final int COLUMN_STARS = 0;
    private static final int COLUMN_DESCRIPTION = 1;
    private static final int COLUMN_ATTAINTMENT_DATE = 2;
    private final String[] columnNames = {"Bonus", "Descripción", "Fecha consecución"};
    private final Class[] collumnDataType = new Class[]{JButton.class, String.class, LocalDate.class};
    private ArrayList<Bonus> bonuses;

    public BonusTableModel(ArrayList<Bonus> bonuses) {
        this.bonuses = bonuses;
    }

    public void setData(ArrayList<Bonus> bonuses) {
        this.bonuses = bonuses;
        Clients.bonuses = bonuses;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return bonuses.size();
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
        Bonus bonus = bonuses.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_STARS -> {
                JButton starsButton = new JButton();
                if (bonus.getBonusType() != null) {
                    switch (bonus.getBonusType()) {
                        case ONE_STAR -> starsButton.setName("one_star");
                        case TWO_STARS -> starsButton.setName("two_stars");
                        case THREE_STARS -> starsButton.setName("three_stars");
                        case FOUR_STARS -> starsButton.setName("four_stars");
                        case FIVE_STARS -> starsButton.setName("five_stars");
                    }
                } else {
                    switch (bonus.getNumericalBonusType()) {
                        case 5 -> starsButton.setName("one_star");
                        case 10 -> starsButton.setName("two_stars");
                        case 15 -> starsButton.setName("three_stars");
                        case 20 -> starsButton.setName("four_stars");
                        case 25 -> starsButton.setName("five_stars");
                    }
                }
                returnValue = starsButton;
            }
            case COLUMN_DESCRIPTION -> returnValue = bonus.getDescription();
            case COLUMN_ATTAINTMENT_DATE -> returnValue = bonus.getAttaintmentDate();
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}