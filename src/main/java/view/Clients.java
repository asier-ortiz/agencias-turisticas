package view;

import com.github.lgooddatepicker.components.DatePicker;
import gui.BonusTableModel;
import gui.ClientTableModel;
import gui.TableListener;
import gui.TourTableModel;
import model.*;
import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.MethodsUtil;

import javax.swing.*;
import java.util.*;

import static util.MethodsUtil.*;

public class Clients extends JFrame {
    private JPanel clientsWindow;
    private JTable clientsTable;
    private JButton newClientButton;
    private JPanel headerPanel;
    private JPanel clientsTitlePanel;
    private JPanel clientsTablePanel;
    private JLabel companyLogo;
    private JTabbedPane clientTabbedPane;
    private JPanel clientDetailsPanel;
    private JTable toursTable;
    private JList<String> historicJlist;
    private JPanel clientDetailTitlePanel;
    private JButton reloadButton;
    private JLabel clientDetailLabel;
    private JButton filterButton;
    private JPanel filterPanel;
    private JTextField idTextField;
    private JTextField dniTextField;
    private JTextField nameTextField;
    private JTextField firstSurnameTextField;
    private DatePicker birthDatePicker;
    private JTextField professionTextField;
    private JButton searchButton;
    private JButton clearButton;
    private JCheckBox activeCheckBox;
    private JButton dismissReenrollButton;
    private JTextField secondSurnameTextField;
    private JTable bonusesTable;
    private JLabel userRoleLabel;
    private Client selectedClient;
    public static ArrayList<Client> clientList;
    private ArrayList<Tour> tourList = new ArrayList<>();
    protected String[] searchFields = {"CLIENT_ID", "DNI", "NAME", "FIRST_SURNAME", "SECOND_SURNAME", "BIRTH_DATE", "PROFESSION", "ACTIVE"};
    public static ClientTableModel clientTableModel;
    private TourTableModel tourTableModel;
    private TableListener tableListener;
    private BonusTableModel bonusTableModel;
    public static ArrayList<Bonus> bonuses = new ArrayList<>();

    public JPanel getClientsWindow() {
        userRoleLabel.setText(Login.connectedEmployee.getName() + " " + Login.connectedEmployee.getFirstSurname() + " - " + Login.connectedEmployee.getRole());
        dismissReenrollButton.setEnabled(false);
        filterPanel.setVisible(false);
        clientList = DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true);
        clientsTable.setName("clientsTable");
        clientTableModel = new ClientTableModel(clientList);
        clientsTable.setModel(clientTableModel);
        tableListener = new TableListener(clientsTable);
        clientsTable.getModel().addTableModelListener(tableListener);
        setTableLookAndFeel(clientsTable, true);
        toursTable.setName("toursTable");
        tourTableModel = new TourTableModel(tourList);
        toursTable.setModel(tourTableModel);
        setTableLookAndFeel(toursTable, true);
        bonusesTable.setName("bonusesTable");
        bonusTableModel = new BonusTableModel(bonuses);
        bonusesTable.setModel(bonusTableModel);
        setTableLookAndFeel(bonusesTable, false);
        setButtonsIconsAndSize();
        return clientsWindow;
    }

    public Clients() {

        reloadButton.addActionListener(e -> reloadData(DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true)));

        dismissReenrollButton.addActionListener(e -> {
            if (selectedClient.isActive()) {
                if (DBConnectionUtil.dataBase.dismissClient(selectedClient.getId())) {
                    clientTableModel.removeRow(clientsTable.getSelectedRow());
                }
            } else {
                if (DBConnectionUtil.dataBase.reenrollClient(selectedClient.getId())) {
                    clientTableModel.removeRow(clientsTable.getSelectedRow());
                }
            }
        });

        clientsTable.getSelectionModel().addListSelectionListener(event -> {
            if (clientsTable.getSelectedRow() > -1) {
                dismissReenrollButton.setEnabled(true);
                selectedClient = clientList.get(clientsTable.getSelectedRow());
                clientDetailLabel.setText("Detalle cliente #" + selectedClient.getId());
                tourList = DBConnectionUtil.dataBase.getAllToursAttendedBy(selectedClient);
                tourTableModel.setData(tourList);
                toursTable.setModel(tourTableModel);
                setTableLookAndFeel(toursTable, true);
                historicJlist.setModel(defaultListModelRegistrationCancelation(selectedClient));
                bonuses = DBConnectionUtil.dataBase.getAllBonussesAchievedThisYearBy(selectedClient);
                bonusTableModel.setData(bonuses);
                bonusesTable.setModel(bonusTableModel);
                setTableLookAndFeel(bonusesTable, false);
            } else {
                dismissReenrollButton.setEnabled(false);
                tourList = new ArrayList<>();
                tourTableModel.setData(tourList);
                toursTable.setModel(tourTableModel);
                setTableLookAndFeel(toursTable, true);
                historicJlist.setModel(new DefaultListModel<>());
                bonuses = new ArrayList<>();
                bonusTableModel.setData(bonuses);
                bonusesTable.setModel(bonusTableModel);
                setTableLookAndFeel(bonusesTable, false);
            }
        });

        newClientButton.addActionListener(e -> {
            setTitle("Nuevo cliente");
            setContentPane(new NewClient().getNewClientWindow());
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            pack();
            setVisible(true);
        });

        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

        searchButton.addActionListener(e -> {
            Integer id;
            try {
                id = Integer.parseInt(idTextField.getText().trim());
            } catch (NumberFormatException ex) {
                id = null;
            }
            List<String> searchFieldsValues = Arrays.asList(
                    id == null ? "" : String.valueOf(id),
                    dniTextField.getText().trim(),
                    nameTextField.getText().trim(),
                    firstSurnameTextField.getText().trim(),
                    secondSurnameTextField.getText().trim(),
                    birthDatePicker.getDate() == null ? "" : formatDate(birthDatePicker.getDate()),
                    professionTextField.getText().trim(),
                    activeCheckBox.isSelected() ? "1" : "0"
            );
            Map<String, String> searchFieldValueMap = new HashMap<>();
            for (int i = 0; i < searchFields.length; i++) {
                searchFieldValueMap.put(searchFields[i], searchFieldsValues.get(i));
            }
            ArrayList<Client> result = DBConnectionUtil.dataBase.getFilteredClients(searchFieldValueMap);
            if (!result.isEmpty()) {
                reloadData(result);
            } else {
                DialogManagerUtil.showErrorDialog("Sin resultados...");
            }
        });

        clearButton.addActionListener(e -> clearInputsData());
    }

    public void clearInputsData() {
        idTextField.setText("");
        dniTextField.setText("");
        nameTextField.setText("");
        firstSurnameTextField.setText("");
        secondSurnameTextField.setText("");
        birthDatePicker.setDate(null);
        professionTextField.setText("");
        activeCheckBox.setSelected(true);
        reloadData(DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true));
    }

    public void reloadData(ArrayList<Client> clientList) {
        Clients.clientList = clientList;
        clientTableModel.setData(clientList);
        clientsTable.setModel(clientTableModel);
        clientsTable.getModel().addTableModelListener(tableListener);
        setTableLookAndFeel(clientsTable, true);
        tourTableModel.setData(new ArrayList<>());
        toursTable.setModel(tourTableModel);
        setTableLookAndFeel(toursTable, true);
        bonuses = new ArrayList<>();
        bonusTableModel.setData(bonuses);
        bonusesTable.setModel(bonusTableModel);
        setTableLookAndFeel(bonusesTable, false);
        historicJlist.setModel(new DefaultListModel<>());
    }

    private void setButtonsIconsAndSize() {
        ImageIcon newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\new.png"), 35, 35);
        newClientButton.setIcon(newIcon);
        newClientButton.setOpaque(false);
        newClientButton.setContentAreaFilled(false);
        newClientButton.setBorderPainted(false);
        newClientButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\filter.png"), 35, 35);
        filterButton.setIcon(newIcon);
        filterButton.setOpaque(false);
        filterButton.setContentAreaFilled(false);
        filterButton.setBorderPainted(false);
        filterButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\reload.png"), 35, 35);
        reloadButton.setIcon(newIcon);
        reloadButton.setOpaque(false);
        reloadButton.setContentAreaFilled(false);
        reloadButton.setBorderPainted(false);
        reloadButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\magnifyingglass.png"), 35, 35);
        searchButton.setIcon(newIcon);
        searchButton.setOpaque(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\broom.png"), 35, 35);
        clearButton.setIcon(newIcon);
        clearButton.setOpaque(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setBorderPainted(false);
        clearButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\switch.png"), 35, 35);
        dismissReenrollButton.setIcon(newIcon);
        dismissReenrollButton.setOpaque(false);
        dismissReenrollButton.setContentAreaFilled(false);
        dismissReenrollButton.setBorderPainted(false);
        dismissReenrollButton.setFocusPainted(false);
        switch (Login.selectedCompany) {
            case 0 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_one.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 1 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_two.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 2 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_three.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
            case 3 -> {
                newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\agency_four.png"), 75, 50);
                companyLogo.setIcon(newIcon);
                companyLogo.setText("");
            }
        }
    }

    private DefaultListModel<String> defaultListModelRegistrationCancelation(Client client) {
        DefaultListModel<String> defaultListModelRegistrationCancelation = new DefaultListModel<>();
        DBConnectionUtil.dataBase.getClientRegistrationsCancellations(client).stream().sorted().forEach(defaultListModelRegistrationCancelation::addElement);
        return defaultListModelRegistrationCancelation;
    }
}