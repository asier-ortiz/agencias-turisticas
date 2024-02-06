package view;

import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.MethodsUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Principal extends JFrame {
    private JPanel principalWindow;
    private JButton employeesButton;
    private JButton clientsButton;
    private JButton toursButton;
    private JButton dataBaseButton;
    private JButton logOutButton;
    private JPanel cards;
    private JPanel MenuPanel;
    private CardLayout cardLayout;
    public static final String EMPLOYEES = "Employees";
    public static final String CLIENTS = "Clients";
    public static final String TOURS = "Tours";
    public static final String DATABASE = "Database";
    private ArrayList<JButton> buttons;

    public JPanel getPrincipalWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buttons = new ArrayList<>(Arrays.asList(clientsButton, employeesButton, toursButton, dataBaseButton, logOutButton));
        cardLayout = new CustomCardLayout();
        cards.setLayout(cardLayout);
        cards.add(createClientsPanel(), CLIENTS);
        cards.add(createEmployeesPanel(), EMPLOYEES);
        cards.add(createToursPanel(), TOURS);
        cards.add(createDataBasePanel(), DATABASE);
        setButtonsIconsSize();
        return principalWindow;
    }

    public Principal() {
        clientsButton.addActionListener(e -> swapView("Clients"));
        employeesButton.addActionListener(e -> {
            if (Login.connectedEmployee.getRole().equals("ADMIN")) {
                swapView("Employees");
            } else {
                DialogManagerUtil.showErrorDialog("Error: Privilegios insuficientes");
            }
        });
        toursButton.addActionListener(e -> swapView("Tours"));
        dataBaseButton.addActionListener(e -> swapView("Database"));
        logOutButton.addActionListener(e -> {
            DBConnectionUtil.dataBase.logEmployeeClockOut(Login.sessionId);
            createLoginPanel();
        });
    }

    private void setButtonsIconsSize() {
        ArrayList<ImageIcon> icons = new ArrayList<>(Arrays.asList(
                MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\clients.png"), 50, 50),
                MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\employees.png"), 50, 50),
                MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\tour.png"), 50, 50),
                MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\database.png"), 50, 50),
                MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\logout.png"), 50, 50)
        ));
        for (int i = 0; i < icons.size(); i++) {
            ImageIcon icon = icons.get(i);
            JButton button = buttons.get(i);
            button.setIcon(icon);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }
    }

    private JPanel createClientsPanel() {
        return new Clients().getClientsWindow();
    }

    private JPanel createEmployeesPanel() {
        return new Employees().getEmployeesWindow();
    }

    private JPanel createToursPanel() {
        return new Tours().getTourssWindow();
    }

    private JPanel createDataBasePanel() {
        return new DataBaseInfo().getDataBaseWindow();
    }

    private void createLoginPanel() {
        Arrays.asList(Window.getWindows()).forEach(Window::dispose);
        Login login = new Login();
        setContentPane(login.getLoginWindow());
        pack();
        setLocationRelativeTo(null);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void swapView(String key) {
        switch (key) {
            case "Clients" -> {
                cards.remove(0);
                cards.add(createClientsPanel(), CLIENTS);
            }
            case "Employees" -> {
                cards.remove(1);
                cards.add(createEmployeesPanel(), EMPLOYEES);
            }
            case "Tours" -> {
                cards.remove(2);
                cards.add(createToursPanel(), TOURS);
            }
            case "Database" -> {
                cards.remove(3);
                cards.add(createDataBasePanel(), DATABASE);
            }
        }
        cardLayout.show(cards, key);
    }

    public static class CustomCardLayout extends CardLayout {

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Component current = findCurrentComponent(parent);
            if (current != null) {
                Insets insets = parent.getInsets();
                Dimension pref = current.getPreferredSize();
                pref.width += insets.left + insets.right;
                pref.height += insets.top + insets.bottom;
                return pref;
            }
            return super.preferredLayoutSize(parent);
        }

        public Component findCurrentComponent(Container parent) {
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }
}