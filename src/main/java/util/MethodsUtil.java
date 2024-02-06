package util;

import gui.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MethodsUtil {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#,##");

    public static String formatDate(LocalDate date) {
        return date.format(dateTimeFormatter);
    }

    public static String formatDouble(double d) {
        return decimalFormatter.format(d);
    }

    public static ImageIcon resizeImageIcon(ImageIcon imageIcon, int width, int height) {
        Image img = imageIcon.getImage();
        Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    public static void setApplicationLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void setTableLookAndFeel(JTable table, boolean sortable) {
        TableColumnModel tableColumnModel = table.getColumnModel();
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new TableCellRenderer());
            if (table.getName() != null) {
                if (table.getName().equals("employeesTable")) {
                    switch (i) {
                        case 1, 2, 3, 5, 6 -> table.getColumnModel().getColumn(i).setCellEditor(new TextCellEditor(new JTextField()));
                        case 4 -> table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
                        case 7 -> table.getColumnModel().getColumn(i).setCellEditor(new ComboBoxCellEditor("TableRolOption"));
                    }
                }
                if (table.getName().equals("clientsTable")) {
                    switch (i) {
                        case 1, 2, 3, 4, 6 -> table.getColumnModel().getColumn(i).setCellEditor(new TextCellEditor(new JTextField()));
                        case 5 -> table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
                    }
                }
                if (table.getName().equals("toursTable")) {
                    switch (i) {
                        case 2, 3, 5, 6, 7, 9 -> table.getColumnModel().getColumn(i).setCellEditor(new TextCellEditor(new JTextField()));
                        case 4 -> table.getColumnModel().getColumn(i).setCellEditor(new ComboBoxCellEditor("TableTopicOption"));
                        case 8 -> {
                            table.getColumnModel().getColumn(i).setCellEditor(new DateTimeCellEditor());
                            table.getColumnModel().getColumn(i).setPreferredWidth(200);
                        }
                    }
                }
            }
        }
        table.setRowHeight(50);
        table.setSelectionBackground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 16));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(true);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setEnabled(sortable);
    }
}