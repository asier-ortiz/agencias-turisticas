package view;

import util.DBConnectionUtil;
import util.MethodsUtil;

import javax.swing.*;
import java.awt.*;

public class DataBaseInfo extends JFrame {
    private JPanel dataBaseWindow;
    private JList<String> dataBaseInfoJlist;
    private JLabel dataBaseLabel;

    public JPanel getDataBaseWindow() {
        dataBaseInfoJlist.setFont(new Font("monospaced", Font.PLAIN, 16));
        dataBaseInfoJlist.setEnabled(false);
        dataBaseInfoJlist.setModel(defaultListModelDataBaseInfo());
        setDataBaseIcon();
        return dataBaseWindow;
    }

    private void setDataBaseIcon() {
        ImageIcon newIcon;
        switch (DBConnectionUtil.dbOption) {
            case 1 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\oracle.png"), 100, 75);
                dataBaseLabel.setIcon(newIcon);
            }
            case 2 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\sqlite.png"), 100, 75);
                dataBaseLabel.setIcon(newIcon);
            }
            case 3 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\mysql.png"), 100, 75);
                dataBaseLabel.setIcon(newIcon);
            }
            case 4 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\db4o.png"), 100, 75);
                dataBaseLabel.setIcon(newIcon);
            }
        }
    }

    private DefaultListModel<String> defaultListModelDataBaseInfo() {
        DefaultListModel<String> defaultListModelRegistrationCancelation = new DefaultListModel<>();
        DBConnectionUtil.dataBase.getDataBaseMetaData().forEach(defaultListModelRegistrationCancelation::addElement);
        return defaultListModelRegistrationCancelation;
    }
}