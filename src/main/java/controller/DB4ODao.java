package controller;

import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.query.Predicate;
import model.*;
import com.db4o.ObjectSet;
import util.DB4OUtil;
import util.DBConnectionUtil;
import util.DialogManagerUtil;

import javax.swing.*;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DB4ODao implements DataBase {

    private static DB4ODao db4ODao;

    private DB4ODao() {
    }

    private synchronized static void createInstance() {
        if (db4ODao == null) {
            db4ODao = new DB4ODao();
        }
    }

    public static DB4ODao getInstance() {
        createInstance();
        return db4ODao;
    }

    /*EMPLOYEE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int getLastEmployeeId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<Employee> employeeObjectSet = db.queryByExample(new Employee());
        for (Employee employee : employeeObjectSet) {
            if (employee.getId() >= id) {
                id = employee.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public Employee login(String email, String password) {
        Employee employee;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Employee> result = db.query(new Predicate<>() {
            public boolean match(Employee employee) {
                return employee.getEmail().equals(email) && employee.getPassword().equals(password) && employee.isActive();
            }
        });
        if (result.size() > 0) {
            employee = result.get(0);
            return employee;
        }
        return null;
    }

    @Override
    public boolean insertEmployee(Employee employee) {
        employee.setId(getLastEmployeeId());
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        EmployeeRegistrationCancellation employeeRegistrationCancellation = new EmployeeRegistrationCancellation(
                getLastEmployeeRegistrationCancellationId(),
                employee,
                LocalDate.now(),
                null);
        employee.getEmployeeRegistrationCancellations().add(employeeRegistrationCancellation);
        db.store(employee);
        db.commit();
        return true;
    }

    @Override
    public Employee getEmployee(int id) {
        Employee employee;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Employee> result = db.query(new Predicate<>() {
            public boolean match(Employee employee) {
                return employee.getId() == id;
            }
        });
        employee = result.get(0);
        return employee;
    }

    @Override
    public ArrayList<Employee> getAllEmployeesWhereActiveIs(boolean active) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Employee> result = db.query(new Predicate<>() {
            public boolean match(Employee employee) {
                return employee.isActive() == active;
            }
        });
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Employee> getFilteredEmployees(Map<String, String> map) {
        Employee employee = new Employee();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "EMPLOYEE_ID":
                        employee.setId(Integer.parseInt(entry.getValue()));
                        break;
                    case "DNI":
                        employee.setDni(entry.getValue());
                        break;
                    case "NAME":
                        employee.setName(entry.getValue());
                        break;
                    case "FIRST_SURNAME":
                        employee.setFirstSurname(entry.getValue());
                        break;
                    case "BIRTH_DATE":
                        employee.setBirthDate(LocalDate.parse(entry.getValue()));
                        break;
                    case "NATIONALITY":
                        employee.setNationality(entry.getValue());
                        break;
                    case "ACTIVE":
                        employee.setActive(entry.getValue().equals("1")); // TODO ESTE VA MAL https://stackoverflow.com/questions/46121363/query-by-example-and-boolean-field
                        break;
                    case "EMAIL":
                        employee.setEmail(entry.getValue());
                        break;
                    case "ROLE":
                        if (!entry.getValue().equals("TODOS")) {
                            employee.setRole(entry.getValue());
                        }
                        break;
                }
            }
        }
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Employee> result = db.queryByExample(employee);
        System.out.println(result.size());
        return new ArrayList<>(result);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        db.store(employee);
        db.commit();
        return true;
    }

    @Override
    public boolean dismissEmployee(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea dar de baja a este empleado?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Employee employee = getEmployee(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            employee.setActive(false);
            List<EmployeeRegistrationCancellation> employeeRegistrationCancellationList = employee.getEmployeeRegistrationCancellations().stream().sorted().collect(Collectors.toList());
            employeeRegistrationCancellationList.get(employeeRegistrationCancellationList.size() - 1).setEndDate(LocalDate.now());
            db.store(employee);
            db.commit();
            DialogManagerUtil.showInfoDialog("Empleado dado de baja");
            return true;
        }
        return false;
    }

    @Override
    public boolean reemployEmployee(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea volver a dar de alta a este empleado?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Employee employee = getEmployee(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            employee.setActive(true);
            EmployeeRegistrationCancellation employeeRegistrationCancellation = new EmployeeRegistrationCancellation(
                    getLastEmployeeRegistrationCancellationId(),
                    employee,
                    LocalDate.now(),
                    null);
            employee.getEmployeeRegistrationCancellations().add(employeeRegistrationCancellation);
            db.store(employee);
            db.commit();
            DialogManagerUtil.showInfoDialog("Empleado dado de alta");
            return true;
        }
        return false;
    }

    /*CLIENT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int getLastClientId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<Client> clientObjectSet = db.queryByExample(new Client());
        for (Client client : clientObjectSet) {
            if (client.getId() >= id) {
                id = client.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public boolean insertClient(Client client) {
        client.setId(getLastClientId());
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ClientRegistrationCancelation clientRegistrationCancelation = new ClientRegistrationCancelation(
                getLastClientRegistrationCancellationId(),
                client,
                LocalDate.now(),
                null);
        client.getClientRegistrationCancelations().add(clientRegistrationCancelation);
        db.store(client);
        db.commit();
        return true;
    }

    public Client getClient(int id) {
        Client client;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Client> result = db.query(new Predicate<>() {
            public boolean match(Client client) {
                return client.getId() == id;
            }
        });
        client = result.get(0);
        return client;
    }

    @Override
    public boolean updateClient(Client client) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        db.store(client);
        db.commit();
        return true;
    }

    @Override
    public boolean dismissClient(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea dar de baja a este cliente?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Client client = getClient(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            client.setActive(false);
            List<ClientRegistrationCancelation> clientRegistrationCancelationList = client.getClientRegistrationCancelations().stream().sorted().collect(Collectors.toList());
            clientRegistrationCancelationList.get(clientRegistrationCancelationList.size() - 1).setEndDate(LocalDate.now());
            db.store(client);
            db.commit();
            DialogManagerUtil.showInfoDialog("Cliente dado de baja");
            return true;
        }
        return false;
    }

    @Override
    public boolean reenrollClient(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea volver a dar de alta a este cliente?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Client client = getClient(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            client.setActive(true);
            ClientRegistrationCancelation clientRegistrationCancelation = new ClientRegistrationCancelation(
                    getLastClientRegistrationCancellationId(),
                    client,
                    LocalDate.now(),
                    null);
            client.getClientRegistrationCancelations().add(clientRegistrationCancelation);
            db.store(client);
            db.commit();
            DialogManagerUtil.showInfoDialog("Cliente dado de alta");
            return true;
        }
        return false;
    }

    @Override
    public boolean singUpClientForTour(Client client, Tour tour) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        Client foundClient = getClient(client.getId());
        Tour foundTour = getTour(tour.getId());
        foundClient.getTours().add(foundTour);
        foundTour.getClients().add(client);
        db.store(foundClient);
        db.store(foundTour);
        foundClient = getClient(foundClient.getId());
        int attendedToursCount = 0;
        final LocalDate start = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
        final LocalDate end = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 31);
        final int days = (int) start.until(end, ChronoUnit.DAYS);
        List<LocalDate> currentYearDates = Stream
                .iterate(start, d -> d.plusDays(1))
                .limit(days)
                .collect(Collectors.toList());
        for (Tour t : foundClient.getTours()) {
            if (currentYearDates.contains(t.getStartDate().toLocalDate())) {
                attendedToursCount++;
            }
        }
        switch (attendedToursCount) {
            case 5 -> {
                Bonus bonus = new Bonus();
                bonus.setId(getLastBonusId());
                bonus.setClient(foundClient);
                bonus.setBonusType(Bonus.BonusType.ONE_STAR);
                bonus.setDescription(Bonus.BonusType.ONE_STAR.getDescription());
                bonus.setAttaintmentDate(LocalDate.now());
                foundClient.getBonuses().add(bonus);
                db.store(foundClient);
                db.store(bonus);
            }
            case 10 -> {
                Bonus bonus = new Bonus();
                bonus.setId(getLastBonusId());
                bonus.setClient(foundClient);
                bonus.setBonusType(Bonus.BonusType.TWO_STARS);
                bonus.setDescription(Bonus.BonusType.TWO_STARS.getDescription());
                bonus.setAttaintmentDate(LocalDate.now());
                foundClient.getBonuses().add(bonus);
                db.store(foundClient);
                db.store(bonus);
            }
            case 15 -> {
                Bonus bonus = new Bonus();
                bonus.setId(getLastBonusId());
                bonus.setClient(foundClient);
                bonus.setBonusType(Bonus.BonusType.THREE_STARS);
                bonus.setDescription(Bonus.BonusType.THREE_STARS.getDescription());
                bonus.setAttaintmentDate(LocalDate.now());
                foundClient.getBonuses().add(bonus);
                db.store(foundClient);
                db.store(bonus);
            }
            case 20 -> {
                Bonus bonus = new Bonus();
                bonus.setId(getLastBonusId());
                bonus.setClient(foundClient);
                bonus.setBonusType(Bonus.BonusType.FOUR_STARS);
                bonus.setDescription(Bonus.BonusType.FOUR_STARS.getDescription());
                bonus.setAttaintmentDate(LocalDate.now());
                foundClient.getBonuses().add(bonus);
                db.store(foundClient);
                db.store(bonus);
            }
            case 25 -> {
                Bonus bonus = new Bonus();
                bonus.setId(getLastBonusId());
                bonus.setClient(foundClient);
                bonus.setBonusType(Bonus.BonusType.FIVE_STARS);
                bonus.setDescription(Bonus.BonusType.FIVE_STARS.getDescription());
                bonus.setAttaintmentDate(LocalDate.now());
                foundClient.getBonuses().add(bonus);
                db.store(foundClient);
                db.store(bonus);
            }
        }
        db.commit();
        DialogManagerUtil.showInfoDialog("Cliente apuntado");
        return true;
    }

    @Override
    public boolean unsubscribeClientForTour(Client client, Tour tour) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        Client foundClient = getClient(client.getId());
        Tour foundTour = getTour(tour.getId());
        foundClient.getTours().remove(foundTour);
        foundTour.getClients().remove(foundClient);
        db.store(foundClient);
        db.store(foundTour);
        db.commit();
        DialogManagerUtil.showInfoDialog("Cliente desapuntado");
        return true;
    }

    @Override
    public ArrayList<Client> getAllClientsWhereActiveIs(boolean active) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Client> result = db.query(new Predicate<>() {
            public boolean match(Client client) {
                return client.isActive() == active;
            }
        });
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Client> getFilteredClients(Map<String, String> map) {
        Client client = new Client();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "CLIENT_ID" -> client.setId(Integer.parseInt(entry.getValue()));
                    case "DNI" -> client.setDni(entry.getValue());
                    case "NAME" -> client.setName(entry.getValue());
                    case "FIRST_SURNAME" -> client.setFirstSurname(entry.getValue());
                    case "SECOND_SURNAME" -> client.setSecondSurname(entry.getValue());
                    case "BIRTH_DATE" -> client.setBirthDate(LocalDate.parse(entry.getValue()));
                    case "PROFESSION" -> client.setProfession(entry.getValue());
                    case "ACTIVE" -> client.setActive(entry.getValue().equals("1"));
                }
            }
        }
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Client> result = db.queryByExample(client);
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Client> getAllAttendeesForTour(Tour tour) {
        Tour foundTour = getTour(tour.getId());
        return foundTour.getClients().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public double getClientAnnualExpense(Client client) {
        double total = 0.0;
        Client foundClient = getClient(client.getId());
        ArrayList<Tour> tours = new ArrayList<>();
        foundClient.getTours()
                .stream()
                .filter(tour -> tour.getStartDate().getYear() == LocalDate.now().getYear())
                .forEach(tours::add);
        Collections.sort(tours);
        for (Tour tour : tours) {
            total += tour.getPrice();
        }
        return total;
    }

    @Override
    public int getClientAnnualDiscountPercentage(Client client) {
        Client foundClient = getClient(client.getId());
        ArrayList<Bonus> bonuses = new ArrayList<>();
        foundClient.getBonuses()
                .stream()
                .filter(bonus -> bonus.getAttaintmentDate().getYear() == LocalDate.now().getYear())
                .forEach(bonuses::add);
        Collections.sort(bonuses);
        if (bonuses.size() > 0) {
            return bonuses.get(0).getBonusType().getDiscount();
        }
        return 0;
    }

    /*TOUR*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int getLastTourId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<Tour> tourObjectSet = db.queryByExample(new Tour());
        for (Tour tour : tourObjectSet) {
            if (tour.getId() >= id) {
                id = tour.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public boolean insertTour(Tour tour) {
        tour.setId(getLastTourId());
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        db.store(tour);
        db.commit();
        return true;
    }

    public Tour getTour(int id) {
        Tour tour;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Tour> result = db.query(new Predicate<>() {
            public boolean match(Tour tour) {
                return tour.getId() == id;
            }
        });
        tour = result.get(0);
        return tour;
    }

    @Override
    public boolean updateTour(Tour tour) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        db.store(tour);
        db.commit();
        return true;
    }

    @Override
    public boolean cancelTour(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea cancelar esta visita?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Tour tour = getTour(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            tour.setCancelled(true);
            db.store(tour);
            db.commit();
            DialogManagerUtil.showInfoDialog("Visita cancelada");
            return true;
        }
        return false;
    }

    @Override
    public boolean reactivateTour(int id) {
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea reactivar esta visita?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            Tour tour = getTour(id);
            ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
            tour.setCancelled(false);
            db.store(tour);
            db.commit();
            DialogManagerUtil.showInfoDialog("Visita reactivda");
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Tour> getAllActiveTours() {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Tour> result = db.query(new Predicate<>() {
            public boolean match(Tour tour) {
                return !tour.isCancelled();
            }
        });
        ArrayList<Tour> tours = new ArrayList<>(result);
        Collections.sort(tours);
        return tours;
    }

    @Override
    public ArrayList<Tour> getAllToursCreatedBy(Employee employee) {
        Employee foundEmployee = getEmployee(employee.getId());
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Tour> result = db.query(new Predicate<>() {
            public boolean match(Tour tour) {
                return tour.getEmployee() == foundEmployee;
            }
        });
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Tour> getAllToursAttendedBy(Client client) {
        Client foundClient = getClient(client.getId());
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Tour> result = db.query(new Predicate<>() {
            public boolean match(Tour tour) {
                return tour.getClients().contains(foundClient);
            }
        });
        return new ArrayList<>(result);
    }

    @Override
    public ArrayList<Tour> getFilteredTours(Map<String, String> map) {
        Tour tour = new Tour();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "TOUR_ID":
                        tour.setId(Integer.parseInt(entry.getValue()));
                        break;
                    case "TITLE":
                        tour.setTitle(entry.getValue());
                        break;
                    case "PLACE":
                        tour.setPlace(entry.getValue());
                        break;
                    case "STARTING_POINT":
                        tour.setStartingPoint(entry.getValue());
                        break;
                    case "MAX_ATTENDEES":
                        tour.setMaxAttendees(Integer.parseInt(entry.getValue()));
                        break;
                    case "PRICE":
                        tour.setPrice(Double.parseDouble(entry.getValue()));
                        break;
                    case "START_DATE":
                        tour.setStartDate(LocalDateTime.parse(entry.getValue()));
                        break;
                    case "CANCELLED":
                        tour.setCancelled(entry.getValue().equals("1"));
                        break;
                    case "EMPLOYEE_ID":
                        if (!entry.getValue().equals("-1")) {
                            tour.setEmployee(getEmployee(Integer.parseInt(entry.getValue())));
                        }
                        break;
                    case "TOPIC":
                        if (!entry.getValue().equals("TODAS")) {
                            tour.setTopic(entry.getValue());
                        }
                        break;
                }
            }
        }
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<Tour> result = db.queryByExample(tour);
        return new ArrayList<>(result);
    }

    @Override
    public double getTourTotalRaised(Tour tour) {
        return tour.getPrice() * tour.getClients().size();
    }

    /*BONUS*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int getLastBonusId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<Bonus> bonusObjectSet = db.queryByExample(new Bonus());
        for (Bonus bonus : bonusObjectSet) {
            if (bonus.getId() >= id) {
                id = bonus.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public ArrayList<Bonus> getAllBonussesAchievedThisYearBy(Client client) {
        Client foundClient = getClient(client.getId());
        return foundClient.getBonuses().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
    }

    /*CANCELLATION_REGISTRATION*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int getLastEmployeeRegistrationCancellationId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<EmployeeRegistrationCancellation> employeeRegistrationCancellationObjectSet = db.queryByExample(new EmployeeRegistrationCancellation());
        for (EmployeeRegistrationCancellation employeeRegistrationCancellation : employeeRegistrationCancellationObjectSet) {
            if (employeeRegistrationCancellation.getId() >= id) {
                id = employeeRegistrationCancellation.getId() + 1;
            }
        }
        return id;
    }

    private int getLastClientRegistrationCancellationId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<ClientRegistrationCancelation> clientRegistrationCancelationObjectSet = db.queryByExample(new ClientRegistrationCancelation());
        for (ClientRegistrationCancelation clientRegistrationCancelation : clientRegistrationCancelationObjectSet) {
            if (clientRegistrationCancelation.getId() >= id) {
                id = clientRegistrationCancelation.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public ArrayList<String> getEmployeeRegistrationCancellation(Employee employee) {
        Employee foundEmployee = getEmployee(employee.getId());
        ArrayList<String> data = new ArrayList<>();
        for (EmployeeRegistrationCancellation employeeRegistrationCancellation : foundEmployee.getEmployeeRegistrationCancellations()) {
            String startDate = String.valueOf(employeeRegistrationCancellation.getStartDate());
            String endDate = String.valueOf(employeeRegistrationCancellation.getEndDate());
            String row = "Fecha contratación " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
            data.add(row);
        }
        return data;
    }

    @Override
    public ArrayList<String> getClientRegistrationsCancellations(Client client) {
        Client foundClient = getClient(client.getId());
        ArrayList<String> data = new ArrayList<>();
        for (ClientRegistrationCancelation clientRegistrationCancelation : foundClient.getClientRegistrationCancelations()) {
            String startDate = String.valueOf(clientRegistrationCancelation.getStartDate());
            String endDate = String.valueOf(clientRegistrationCancelation.getEndDate());
            String row = "Fecha inscripción " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
            data.add(row);
        }
        return data;
    }

    /*EMPLOYEE_CLOCK_IN_CLOCK_OUT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    public EmployeeClockInClockOut getEmployeeClocInClockOut(int id) {
        EmployeeClockInClockOut employeeClockInClockOut;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        List<EmployeeClockInClockOut> result = db.query(new Predicate<>() {
            public boolean match(EmployeeClockInClockOut employeeClockInClockOut1) {
                return employeeClockInClockOut1.getId() == id;
            }
        });
        employeeClockInClockOut = result.get(0);
        return employeeClockInClockOut;
    }

    private int getLastEmployeeClocInClockOutId() {
        int id = 0;
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ObjectSet<EmployeeClockInClockOut> employeeClockInClockOutObjectSet = db.queryByExample(new EmployeeClockInClockOut());
        for (EmployeeClockInClockOut employeeClockInClockOut : employeeClockInClockOutObjectSet) {
            if (employeeClockInClockOut.getId() >= id) {
                id = employeeClockInClockOut.getId() + 1;
            }
        }
        return id;
    }

    @Override
    public Integer logEmployeeClockIn(Employee employee) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        Employee foundEmployee = getEmployee(employee.getId());
        Integer lastId = getLastEmployeeClocInClockOutId();
        EmployeeClockInClockOut employeeClockInClockOut = new EmployeeClockInClockOut(lastId, foundEmployee, Timestamp.from(Instant.now()), null);
        foundEmployee.getEmployeeClockInClockOuts().add(employeeClockInClockOut);
        db.store(foundEmployee);
        db.commit();
        return lastId;
    }

    @Override
    public void logEmployeeClockOut(int sessionId) {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        EmployeeClockInClockOut foundEmployeeClockInClockOut = getEmployeeClocInClockOut(sessionId);
        foundEmployeeClockInClockOut.setEndDate(Timestamp.from(Instant.now()));
        db.store(foundEmployeeClockInClockOut);
        db.commit();
    }

    @Override
    public ArrayList<String> getEmployeeClockInsClockOuts(Employee employee) {
        Employee foundEmployee = getEmployee(employee.getId());
        ArrayList<String> data = new ArrayList<>();
        for (EmployeeClockInClockOut employeeClockInClockOut : foundEmployee.getEmployeeClockInClockOuts()) {
            String startDate = String.valueOf(employeeClockInClockOut.getStartDate());
            String endDate = String.valueOf(employeeClockInClockOut.getEndDate());
            String row = "Entrada " + "(" + startDate + ")" + " || " + "Salida " + "(" + endDate + ")";
            data.add(row);
        }
        return data;
    }

    /*DATABASE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<String> getDataBaseMetaData() {
        ObjectContainer db = DBConnectionUtil.getDataBaseContainer();
        ArrayList<String> data = new ArrayList<>();
        StoredClass[] storedClasses = {db.ext().storedClass(Bonus.class), db.ext().storedClass(Client.class), db.ext().storedClass(ClientRegistrationCancelation.class),
                db.ext().storedClass(Employee.class), db.ext().storedClass(EmployeeClockInClockOut.class), db.ext().storedClass(EmployeeRegistrationCancellation.class), db.ext().storedClass(Tour.class)};
        data.add("INFORMACIÓN SOBRE LA BASE DE DATOS");
        data.add("========================================================");
        data.add("Nombre  :  " + "DB40");
        data.add("Fichero :  " + DB4OUtil.dbFileName);
        data.add("Versión :  " + db.ext().version());
        data.add("========================================================");
        data.add("\n");
        data.add("\n");
        for (StoredClass storedClass : storedClasses) {
            String objectInfo = ("CLASE  -> " + " { Nombre  :  " + storedClass.getName() + " } " + " { Cantidad objetos  :  " + storedClass.instanceCount() + " } ");
            data.add(objectInfo);
            String headers = String.format("|%s %-40s | %s %-40s | ", "", "Variable", "", "Tipo");
            String separator = "+" + String.join("", Collections.nCopies(headers.length() - 3, "-")) + "+";
            data.add(separator);
            data.add(headers);
            data.add(separator);
            for (StoredField storedField : storedClass.getStoredFields()) {
                String variableName = storedField.getName();
                String variableType = storedField.getStoredType().toString();
                String output = String.format("|%s %-40s | %s %-40s | ", "", variableName, "", variableType);
                data.add(output);
            }
            data.add(separator);
            data.add("\n");
            data.add("\n");
        }
        return data;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}