package view;

import com.github.lgooddatepicker.components.DatePicker;
import gui.*;
import model.*;
import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.MethodsUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static util.MethodsUtil.*;

public class Tours extends JFrame {
    private JPanel toursWindow;
    private JTable clientsTable;
    private JButton newTourButton;
    private JPanel headerPanel;
    private JPanel toursTitlePanel;
    private JPanel toursTablePanel;
    private JLabel companyLogo;
    private JTable toursTable;
    private JButton reloadButton;
    private JButton filterButton;
    private JPanel filterPanel;
    private JTextField idTextField;
    private JComboBox<Employee> employeeComboBox;
    private JComboBox<String> topicComboBox;
    private JTextField titleTextField;
    private JButton searchButton;
    private JButton clearButton;
    private JCheckBox cancelledCheckBox;
    private JTextField placeTextField;
    private JButton rightArrowButton;
    private JButton leftArrowbutton;
    private JList<Character> lettersList;
    private JTable clientsTourTable;
    private JTable attendeesTourTable;
    private JTextField startingPointTextField;
    private JTextField maxAttendeesTextField;
    private JTextField priceTextField;
    private DatePicker startingDatePicker;
    private JButton cancelReactivateButton;
    private JLabel userRoleLabel;
    private Tour selectedTour;
    private Client selectedClient;
    private Client selectedAttendee;
    public static ArrayList<Tour> tourList;
    private ArrayList<Client> clientsList;
    private ArrayList<Client> filteredClientsList;
    public static ArrayList<Client> attendeesList;
    public static TourTableModel tourTableModel;
    private ClientTourTableModel clientTourTableModel;
    private ClientTourTableModel attendeesTourTableModel;
    protected String[] searchFields = {"TOUR_ID", "EMPLOYEE_ID", "TITLE", "TOPIC", "PLACE", "STARTING_POINT", "MAX_ATTENDEES", "PRICE", "START_DATE", "CANCELLED"};

    public JPanel getTourssWindow() {
        userRoleLabel.setText(Login.connectedEmployee.getName() + " " + Login.connectedEmployee.getFirstSurname() + " - " + Login.connectedEmployee.getRole());
        cancelReactivateButton.setEnabled(false);
        filterPanel.setVisible(false);
        tourList = DBConnectionUtil.dataBase.getAllActiveTours();
        toursTable.setName("toursTable");
        tourTableModel = new TourTableModel(tourList);
        toursTable.setModel(tourTableModel);
        TableListener tourTableListener = new TableListener(toursTable);
        toursTable.getModel().addTableModelListener(tourTableListener);
        setTableLookAndFeel(toursTable, true);
        clientsList = DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true);
        clientsTourTable.setName("clientsTourTable");
        clientTourTableModel = new ClientTourTableModel(clientsList);
        clientsTourTable.setModel(clientTourTableModel);
        setTableLookAndFeel(clientsTourTable, true);
        attendeesList = new ArrayList<>();
        attendeesTourTable.setName("attendeesTourTable");
        attendeesTourTableModel = new ClientTourTableModel(attendeesList);
        attendeesTourTable.setModel(attendeesTourTableModel);
        setTableLookAndFeel(attendeesTourTable, true);
        lettersList.setModel(defaultListModelClientIndexNameLetter());
        employeeComboBox.addItem(new Employee(-1, "TODOS", ""));
        DBConnectionUtil.dataBase.getAllEmployeesWhereActiveIs(true).stream().sorted().forEach(e -> employeeComboBox.addItem(e));
        topicComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"TODAS", "ACTIVIDAD_FAMILIAR", "CONCIERTO",
                "DANZA_TEATRO", "DEPORTES", "EVENTOS_GASTRONOMICOS", "EXPOSICIONES", "FESTIVALES", "FIESTAS_TRADICIONES", "CONGRESOS_FERIAS", "RUTAS", "OTROS"}));
        setButtonsIconsAndSize();
        return toursWindow;
    }

    public Tours() {

        reloadButton.addActionListener(e -> {
            reloadData(DBConnectionUtil.dataBase.getAllActiveTours());
            cancelledCheckBox.setSelected(false);
        });

        cancelReactivateButton.addActionListener(e -> {
            if (!selectedTour.isCancelled()) {
                if (DBConnectionUtil.dataBase.cancelTour(selectedTour.getId())) {
                    tourTableModel.removeRow(toursTable.getSelectedRow());
                }
            } else {
                if (DBConnectionUtil.dataBase.reactivateTour(selectedTour.getId())) {
                    tourTableModel.removeRow(toursTable.getSelectedRow());
                }
            }
        });

        toursTable.getSelectionModel().addListSelectionListener(event -> {
            if (toursTable.getSelectedRow() > -1) {
                cancelReactivateButton.setEnabled(true);
                selectedTour = tourList.get(toursTable.getSelectedRow());
                if (selectedTour.isCancelled() || selectedTour.getStartDate().isBefore(LocalDateTime.now())) {
                    rightArrowButton.setEnabled(false);
                    leftArrowbutton.setEnabled(false);
                } else {
                    rightArrowButton.setEnabled(true);
                    leftArrowbutton.setEnabled(true);
                }
                attendeesList = DBConnectionUtil.dataBase.getAllAttendeesForTour(selectedTour);
                attendeesTourTableModel.setData(attendeesList);
                attendeesTourTable.setModel(attendeesTourTableModel);
                setTableLookAndFeel(attendeesTourTable, true);
            } else {
                cancelReactivateButton.setEnabled(false);
                selectedTour = null;
                attendeesList = new ArrayList<>();
                attendeesTourTableModel.setData(attendeesList);
                attendeesTourTable.setModel(attendeesTourTableModel);
                setTableLookAndFeel(attendeesTourTable, true);
            }
        });

        clientsTourTable.getSelectionModel().addListSelectionListener(event -> {
            if (clientsTourTable.getSelectedRow() > -1) {
                selectedClient = clientsList.get(clientsTourTable.getSelectedRow());
            } else {
                selectedClient = null;
            }
        });

        attendeesTourTable.getSelectionModel().addListSelectionListener(event -> {
            if (attendeesTourTable.getSelectedRow() > -1) {
                selectedAttendee = attendeesList.get(attendeesTourTable.getSelectedRow());
            } else {
                selectedAttendee = null;
            }
        });

        rightArrowButton.addActionListener(e -> {
            if (selectedTour == null) {
                DialogManagerUtil.showErrorDialog("Has de seleccionar a una visita primero");
            } else if (selectedClient == null) {
                DialogManagerUtil.showErrorDialog("Has de seleccionar a un cliente primero para poder añadirlo");
            } else if (attendeesList.contains(selectedClient)) {
                DialogManagerUtil.showErrorDialog("El cliente seleccionado ya está apuntado en esta visita");
            } else if (attendeesList.size() >= selectedTour.getMaxAttendees()) {
                DialogManagerUtil.showErrorDialog("La visita está completa, no se aceptan más clientes");
            } else {
                if (DBConnectionUtil.dataBase.singUpClientForTour(selectedClient, selectedTour)) {
                    attendeesList = DBConnectionUtil.dataBase.getAllAttendeesForTour(selectedTour);
                    attendeesTourTableModel.setData(attendeesList);
                    attendeesTourTable.setModel(attendeesTourTableModel);
                    setTableLookAndFeel(attendeesTourTable, true);
                }
            }
        });

        leftArrowbutton.addActionListener(e -> {
            if (selectedTour == null) {
                DialogManagerUtil.showErrorDialog("Has de seleccionar a una visita primero");
            } else if (selectedAttendee == null) {
                DialogManagerUtil.showErrorDialog("Has de seleccionar a un cliente primero para poder eliminarlo");
            } else {
                if (DBConnectionUtil.dataBase.unsubscribeClientForTour(selectedAttendee, selectedTour)) {
                    attendeesList = DBConnectionUtil.dataBase.getAllAttendeesForTour(selectedTour);
                    attendeesTourTableModel.setData(attendeesList);
                    attendeesTourTable.setModel(attendeesTourTableModel);
                    setTableLookAndFeel(attendeesTourTable, true);
                }
            }
        });

        newTourButton.addActionListener(e -> {
            setTitle("Nueva visita");
            setContentPane(new NewTour().getNewTourWindow());
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
            Integer maxAttendees;
            try {
                maxAttendees = Integer.parseInt(maxAttendeesTextField.getText().trim());
            } catch (NumberFormatException ex) {
                maxAttendees = null;
            }
            Double price;
            try {
                price = Double.parseDouble(priceTextField.getText().trim());
            } catch (NumberFormatException ex) {
                price = null;
            }
            List<String> searchFieldsValues = Arrays.asList(
                    id == null ? "" : String.valueOf(id),
                    String.valueOf(employeeComboBox.getItemAt(employeeComboBox.getSelectedIndex()).getId()),
                    titleTextField.getText().trim(),
                    topicComboBox.getItemAt(topicComboBox.getSelectedIndex()),
                    placeTextField.getText().trim(),
                    startingPointTextField.getText().trim(),
                    maxAttendees == null ? "" : String.valueOf(maxAttendees),
                    price == null ? "" : String.valueOf(price),
                    startingDatePicker.getDate() == null ? "" : formatDate(startingDatePicker.getDate()),
                    cancelledCheckBox.isSelected() ? "1" : "0"
            );
            Map<String, String> searchFieldValueMap = new HashMap<>();
            for (int i = 0; i < searchFields.length; i++) {
                searchFieldValueMap.put(searchFields[i], searchFieldsValues.get(i));
            }
            ArrayList<Tour> result = DBConnectionUtil.dataBase.getFilteredTours(searchFieldValueMap);
            if (!result.isEmpty()) {
                reloadData(result);
            } else {
                DialogManagerUtil.showErrorDialog("Sin resultados...");
            }
        });

        lettersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lettersList.getModel().getSize() > 0) {
                    if (lettersList.getSelectedIndex() != 0) {
                        Map<Character, List<Client>> groupAlphabetically = clientsList.stream().collect(Collectors.groupingBy(client -> client.getName().charAt(0)));
                        filteredClientsList = new ArrayList<>();
                        filteredClientsList.addAll(groupAlphabetically.get(lettersList.getSelectedValue()));
                        clientTourTableModel = new ClientTourTableModel(filteredClientsList);
                        clientsTourTable.setModel(clientTourTableModel);
                        setTableLookAndFeel(clientsTourTable, true);
                    } else {
                        clientTourTableModel = new ClientTourTableModel(clientsList);
                        clientsTourTable.setModel(clientTourTableModel);
                        setTableLookAndFeel(clientsTourTable, true);

                    }
                }
            }
        });

        clearButton.addActionListener(e -> clearInputsData());
    }

    public void clearInputsData() {
        idTextField.setText("");
        employeeComboBox.setSelectedIndex(0);
        titleTextField.setText("");
        topicComboBox.setSelectedIndex(0);
        placeTextField.setText("");
        startingPointTextField.setText("");
        maxAttendeesTextField.setText("");
        cancelledCheckBox.setSelected(false);
        priceTextField.setText("");
        startingDatePicker.setDate(null);
        reloadData(DBConnectionUtil.dataBase.getAllActiveTours());
    }

    public void reloadData(ArrayList<Tour> tours) {
        tourList = tours;
        tourTableModel.setData(tours);
        toursTable.setModel(tourTableModel);
        setTableLookAndFeel(toursTable, true);
        clientsList = DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true);
        clientTourTableModel = new ClientTourTableModel(clientsList);
        clientsTourTable.setModel(clientTourTableModel);
        setTableLookAndFeel(clientsTourTable, true);
        lettersList.setSelectedIndex(0);
    }

    private void setButtonsIconsAndSize() {
        ImageIcon newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\new.png"), 35, 35);
        newTourButton.setIcon(newIcon);
        newTourButton.setOpaque(false);
        newTourButton.setContentAreaFilled(false);
        newTourButton.setBorderPainted(false);
        newTourButton.setFocusPainted(false);
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
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\left_arrow.png"), 50, 50);
        leftArrowbutton.setIcon(newIcon);
        leftArrowbutton.setOpaque(false);
        leftArrowbutton.setContentAreaFilled(false);
        leftArrowbutton.setBorderPainted(false);
        leftArrowbutton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\right_arrow.png"), 50, 50);
        rightArrowButton.setIcon(newIcon);
        rightArrowButton.setOpaque(false);
        rightArrowButton.setContentAreaFilled(false);
        rightArrowButton.setBorderPainted(false);
        rightArrowButton.setFocusPainted(false);
        newIcon = MethodsUtil.resizeImageIcon(new ImageIcon(".\\src\\resources\\switch.png"), 35, 35);
        cancelReactivateButton.setIcon(newIcon);
        cancelReactivateButton.setOpaque(false);
        cancelReactivateButton.setContentAreaFilled(false);
        cancelReactivateButton.setBorderPainted(false);
        cancelReactivateButton.setFocusPainted(false);
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

    private DefaultListModel<Character> defaultListModelClientIndexNameLetter() {
        DefaultListModel<Character> defaultListModelIndiceNombres = new DefaultListModel<>();
        ArrayList<Client> clients = DBConnectionUtil.dataBase.getAllClientsWhereActiveIs(true);
        Set<Character> characterSet = new TreeSet<>();
        characterSet.add('-');
        clients.stream()
                .distinct()
                .map(Client::getName)
                .sorted(String::compareTo)
                .forEach(name -> characterSet.add(name.charAt(0)));
        characterSet.forEach(defaultListModelIndiceNombres::addElement);
        return defaultListModelIndiceNombres;
    }
}