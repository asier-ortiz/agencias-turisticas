package view;

import controller.*;
import model.Employee;
import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.MethodsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Login extends JFrame {
    private JPanel loginWindow;
    private JTextField emailTextField;
    private JPasswordField passwordTextField;
    private JButton loginButton;
    private JButton companyButton1;
    private JButton companyButton2;
    private JButton companyButton3;
    private JButton companyButton4;
    private JLabel companyNameLabel;
    private JTextPane companyDescriptionTextPane;
    private ArrayList<JButton> companiesButtons;
    private final Border border = new LineBorder(Color.GRAY, 4);
    public static Employee connectedEmployee = null;
    public static Integer sessionId = null;
    private final DataBase[] dataBases = {OracleDao.getInstance(), SqliteDAO.getInstance(), MysqlDAO.getInstance(), DB4ODao.getInstance()};
    private final String[] companyNames = {"GUIARTU", "BASKELAND TOURS", "ARTEA EUSKADI TOURS", "INBASQUE"};
    public static int selectedCompany = 0;

    public JPanel getLoginWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        companyButton1.setSelected(true);
        DBConnectionUtil.setDbOption(1);
        DBConnectionUtil.setDataBase(dataBases[0]);
        companiesButtons = new ArrayList<>(Arrays.asList(companyButton1, companyButton2, companyButton3, companyButton4));
        setButtonsIconsSize();
        setButtonsSettings();
        return loginWindow;
    }

    private boolean validateUser() {
        connectedEmployee = DBConnectionUtil.dataBase.login(emailTextField.getText(), String.valueOf(passwordTextField.getPassword()));
        return connectedEmployee != null;
    }

    private void setButtonsSettings() {
        for (int i = 0; i < companiesButtons.size(); i++) {
            int finalI = i;
            JButton companyButton = companiesButtons.get(i);
            companyButton.setSelected(false);
            companyButton.setFocusPainted(false);
            companyButton.addActionListener(e -> {
                DBConnectionUtil.setDbOption(finalI + 1);
                DBConnectionUtil.setDataBase(dataBases[finalI]);
                selectedCompany = finalI;
            });
            companyButton.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    companiesButtons.forEach(companyButton -> companyButton.setBorderPainted(false));
                    companyButton.setText("Company " + finalI);
                    companyButton.setBorder(border);
                    companyButton.setBorderPainted(true);
                    companyNameLabel.setText(companyNames[finalI]);
                    companyNameLabel.setText("Company " + (finalI + 1));
                    if (finalI == 0) {
                        companyNameLabel.setText("Guiartu");
                        companyDescriptionTextPane.setText("""
                                Guiartu:
                                Fundada en 1976
                                Ofrece paseos por el centro histórico de Vitoria-Gasteiz, explicando su historia.
                                """
                        );
                    }
                    if (finalI == 1) {
                        companyNameLabel.setText("Basqueland Tours");
                        companyDescriptionTextPane.setText("""
                                Basqueland Tours:
                                Fundada en 2017
                                Ofrece charlas por el centro histórico de Vitoria-Gasteiz, explicando la historia de los murales pintados que alí se encuentran, así como lo que cada uno representa.
                                """);
                    }
                    if (finalI == 2) {
                        companyNameLabel.setText("Artea");
                        companyDescriptionTextPane.setText("""
                                Artea:
                                Fundada en 2020
                                Ofrece visitas por las diferentes destilerías y bodegas de toda la comunidad.
                                """);
                    }
                    if (finalI == 3) {
                        companyNameLabel.setText("inbasque");
                        companyDescriptionTextPane.setText("""
                                inbasque:
                                Fundada en 2015
                                Organiza tours centrados en el impacto que dejó el franquismo en las estructuras vitorianas.
                                """);
                    }
                    DBConnectionUtil.setDbOption(finalI + 1);
                    DBConnectionUtil.setDataBase(dataBases[finalI]);
                    selectedCompany = finalI;
                }
            });
        }
        loginButton.addActionListener(e -> {
            if (!validateUser()) {
                DialogManagerUtil.showErrorDialog("Nombre de usuario o contraseña incorrectos");
            } else {
                sessionId = DBConnectionUtil.dataBase.logEmployeeClockIn(connectedEmployee);
                Arrays.asList(Window.getWindows()).forEach(Window::dispose);
                Principal principal = new Principal();
                setContentPane(principal.getPrincipalWindow());
                pack();
                setLocationRelativeTo(null);
                setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
                setVisible(true);
            }
        });
    }

    private void setButtonsIconsSize() {
        ImageIcon icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_one.png"), 300, 200);
        companyButton1.setIcon(icon);
        companyButton1.setOpaque(false);
        companyButton1.setContentAreaFilled(false);
        companyButton1.setBorderPainted(false);
        icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_two.png"), 300, 200);
        companyButton2.setIcon(icon);
        companyButton2.setOpaque(false);
        companyButton2.setContentAreaFilled(false);
        companyButton2.setBorderPainted(false);
        icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_three.png"), 300, 200);
        companyButton3.setIcon(icon);
        companyButton3.setOpaque(false);
        companyButton3.setContentAreaFilled(false);
        companyButton3.setBorderPainted(false);
        icon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_four.png"), 300, 200);
        companyButton4.setIcon(icon);
        companyButton4.setOpaque(false);
        companyButton4.setContentAreaFilled(false);
        companyButton4.setBorderPainted(false);
    }
}