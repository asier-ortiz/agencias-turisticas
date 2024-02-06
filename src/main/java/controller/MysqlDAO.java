package controller;

import model.*;
import util.DBConnectionUtil;
import util.DialogManagerUtil;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MysqlDAO implements DataBase {
    private static MysqlDAO mysqlDAO;
    private EntityManager em;

    private MysqlDAO() {
    }

    private synchronized static void createInstence() {
        if (mysqlDAO == null) {
            mysqlDAO = new MysqlDAO();
        }
    }

    public static MysqlDAO getInstance() {
        createInstence();
        return mysqlDAO;
    }

    /*EMPLOYEE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public Employee login(String email, String password) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        Employee employee = null;
        try {
            em.getTransaction().begin();
            employee = (Employee) em.createQuery("FROM Employee WHERE email= :email and password= :password and active= :active")
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .setParameter("active", true)
                    .getResultList()
                    .get(0);
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return employee;
    }

    @Override
    public boolean insertEmployee(Employee employee) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            EmployeeRegistrationCancellation employeeRegistrationCancellation = new EmployeeRegistrationCancellation(employee, LocalDate.now(), null);
            em.persist(employee);
            em.persist(employeeRegistrationCancellation);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public Employee getEmployee(int id) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        Employee employee = null;
        try {
            em.getTransaction().begin();
            employee = em.find(model.Employee.class, id);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return employee;
    }

    @Override
    public ArrayList<Employee> getAllEmployeesWhereActiveIs(boolean active) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        List employees = null;
        try {
            employees = em.createQuery("FROM Employee WHERE active= :active")
                    .setParameter("active", active)
                    .getResultList();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        if (employees != null) {
            return new ArrayList<Employee>(employees);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Employee> getFilteredEmployees(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("FROM Employee WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "EMPLOYEE_ID":
                        sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" and ");
                        break;
                    case "DNI":
                    case "NAME":
                    case "FIRST_SURNAME":
                    case "BIRTH_DATE":
                    case "NATIONALITY":
                    case "EMAIL":
                    case "ACTIVE":
                        sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                        break;
                    case "ROLE":
                        if (!entry.getValue().equals("TODOS")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                        }
                        break;
                }
            }
        }
        if (sb.length() > sbInitialLength) {
            sb.delete(sb.length() - 5, sb.length());
            SQL = sb.toString();
        }
        if (SQL != null) {
            em = HibernateUtil.getSessionFactory().createEntityManager();
            List employees = null;
            try {
                Query query = em.createQuery(SQL);
                employees = query.getResultList();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
            if (employees != null) {
                return new ArrayList<Employee>(employees);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(employee);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                employee.setActive(false);
                List<EmployeeRegistrationCancellation> employeeRegistrationCancellationList = employee.getEmployeeRegistrationCancellations().stream().sorted().collect(Collectors.toList());
                EmployeeRegistrationCancellation employeeRegistrationCancellation = employeeRegistrationCancellationList.get(employeeRegistrationCancellationList.size() - 1);
                employeeRegistrationCancellation.setEndDate(LocalDate.now());
                em.merge(employee);
                em.merge(employeeRegistrationCancellation);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                employee.setActive(true);
                EmployeeRegistrationCancellation employeeRegistrationCancellation = new EmployeeRegistrationCancellation(employee, LocalDate.now(), null);
                em.merge(employee);
                em.persist(employeeRegistrationCancellation);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        return false;
    }

    /*CLIENT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean insertClient(Client client) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ClientRegistrationCancelation clientRegistrationCancelation = new ClientRegistrationCancelation(client, LocalDate.now(), null);
            em.persist(client);
            em.persist(clientRegistrationCancelation);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
    }

    public Client getClient(int id) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        Client client = null;
        try {
            em.getTransaction().begin();
            client = em.find(model.Client.class, id);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return client;
    }

    @Override
    public boolean updateClient(Client client) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(client);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                client.setActive(false);
                List<ClientRegistrationCancelation> clientRegistrationCancelationList = client.getClientRegistrationCancelations().stream().sorted().collect(Collectors.toList());
                ClientRegistrationCancelation clientRegistrationCancelation = clientRegistrationCancelationList.get(clientRegistrationCancelationList.size() - 1);
                clientRegistrationCancelation.setEndDate(LocalDate.now());
                em.merge(client);
                em.merge(clientRegistrationCancelation);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                client.setActive(true);
                ClientRegistrationCancelation clientRegistrationCancelation = new ClientRegistrationCancelation(client, LocalDate.now(), null);
                em.merge(client);
                em.persist(clientRegistrationCancelation);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        return false;
    }

    @Override
    public boolean singUpClientForTour(Client client, Tour tour) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            client.getTours().add(tour);
            tour.getClients().add(client);
            em.merge(client);
            em.merge(tour);
            int attendedToursCount = 0;
            if (tour.getStartDate().getYear() == LocalDate.now().getYear()) {
                attendedToursCount++;
            }
            final LocalDate start = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
            final LocalDate end = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 31);
            final int days = (int) start.until(end, ChronoUnit.DAYS);
            List<LocalDate> currentYearDates = Stream
                    .iterate(start, d -> d.plusDays(1))
                    .limit(days)
                    .collect(Collectors.toList());
            for (Tour t : client.getTours()) {
                if (currentYearDates.contains(t.getStartDate().toLocalDate())) {
                    attendedToursCount++;
                }
            }
            switch (attendedToursCount) {
                case 5 -> {
                    Bonus bonus = new Bonus();
                    bonus.setClient(client);
                    bonus.setNumericalBonusType(Bonus.BonusType.ONE_STAR.getDiscount());
                    bonus.setDescription(Bonus.BonusType.ONE_STAR.getDescription());
                    bonus.setAttaintmentDate(LocalDate.now());
                    em.persist(bonus);
                }
                case 10 -> {
                    Bonus bonus = new Bonus();
                    bonus.setClient(client);
                    bonus.setNumericalBonusType(Bonus.BonusType.TWO_STARS.getDiscount());
                    bonus.setDescription(Bonus.BonusType.TWO_STARS.getDescription());
                    bonus.setAttaintmentDate(LocalDate.now());
                    em.persist(bonus);
                }
                case 15 -> {
                    Bonus bonus = new Bonus();
                    bonus.setClient(client);
                    bonus.setNumericalBonusType(Bonus.BonusType.THREE_STARS.getDiscount());
                    bonus.setDescription(Bonus.BonusType.THREE_STARS.getDescription());
                    bonus.setAttaintmentDate(LocalDate.now());
                    em.persist(bonus);
                }
                case 20 -> {
                    Bonus bonus = new Bonus();
                    bonus.setClient(client);
                    bonus.setNumericalBonusType(Bonus.BonusType.FOUR_STARS.getDiscount());
                    bonus.setDescription(Bonus.BonusType.FOUR_STARS.getDescription());
                    bonus.setAttaintmentDate(LocalDate.now());
                    em.persist(bonus);
                }
                case 25 -> {
                    Bonus bonus = new Bonus();
                    bonus.setClient(client);
                    bonus.setNumericalBonusType(Bonus.BonusType.FIVE_STARS.getDiscount());
                    bonus.setDescription(Bonus.BonusType.FIVE_STARS.getDescription());
                    bonus.setAttaintmentDate(LocalDate.now());
                    em.persist(bonus);
                }
            }
            em.getTransaction().commit();
            DialogManagerUtil.showInfoDialog("Cliente apuntado");
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public boolean unsubscribeClientForTour(Client client, Tour tour) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            client.getTours().remove(tour);
            tour.getClients().remove(client);
            em.merge(client);
            em.merge(tour);
            em.getTransaction().commit();
            DialogManagerUtil.showInfoDialog("Cliente desapuntado");
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public ArrayList<Client> getAllClientsWhereActiveIs(boolean active) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        List clients = null;
        try {
            clients = em.createQuery("FROM Client WHERE active= :active")
                    .setParameter("active", active)
                    .getResultList();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        if (clients != null) {
            return new ArrayList<Client>(clients);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Client> getFilteredClients(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("FROM Client WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "CLIENT_ID" -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" and ");
                    case "DNI", "NAME", "FIRST_SURNAME", "SECOND_SURNAME", "BIRTH_DATE", "PROFESSION", "ACTIVE" -> sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                }
            }
        }
        if (sb.length() > sbInitialLength) {
            sb.delete(sb.length() - 5, sb.length());
            SQL = sb.toString();
        }
        if (SQL != null) {
            em = HibernateUtil.getSessionFactory().createEntityManager();
            List clients = null;
            try {
                Query query = em.createQuery(SQL);
                clients = query.getResultList();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
            if (clients != null) {
                return new ArrayList<Client>(clients);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Client> getAllAttendeesForTour(Tour tour) {
        if (tour.getClients() != null) {
            return new ArrayList<>(tour.getClients());
        }
        return new ArrayList<>();
    }

    @Override
    public double getClientAnnualExpense(Client client) {
        double total = 0.0;
        ArrayList<Tour> tours = new ArrayList<>();
        client.getTours()
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
        ArrayList<Bonus> bonuses = new ArrayList<>();
        client.getBonuses()
                .stream()
                .filter(bonus -> bonus.getAttaintmentDate().getYear() == LocalDate.now().getYear())
                .forEach(bonuses::add);
        Collections.sort(bonuses);
        if (bonuses.size() > 0) {
            return bonuses.get(0).getNumericalBonusType();
        }
        return 0;
    }

    /*TOUR*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean insertTour(Tour tour) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tour);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
    }

    public Tour getTour(int id) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        Tour tour = null;
        try {
            em.getTransaction().begin();
            tour = em.find(model.Tour.class, id);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        return tour;
    }

    @Override
    public boolean updateTour(Tour tour) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(tour);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return false;
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                tour.setCancelled(true);
                em.merge(tour);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
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
            em = HibernateUtil.getSessionFactory().createEntityManager();
            try {
                em.getTransaction().begin();
                tour.setCancelled(false);
                em.merge(tour);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        return false;
    }

    @Override
    public ArrayList<Tour> getAllActiveTours() {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        List tours = null;
        try {
            tours = em.createQuery("FROM Tour WHERE cancelled= :cancelled")
                    .setParameter("cancelled", false)
                    .getResultList();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        if (tours != null) {
            ArrayList<Tour> tourArrayList = new ArrayList<>(tours);
            Collections.sort(tourArrayList);
            return tourArrayList;
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Tour> getAllToursCreatedBy(Employee employee) {
        if (employee.getTours() != null) {
            return new ArrayList<>(employee.getTours());
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Tour> getAllToursAttendedBy(Client client) {
        if (client.getTours() != null) {
            return new ArrayList<>(client.getTours());
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Tour> getFilteredTours(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("FROM Tour WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "TOUR_ID" -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" and ");
                    case "TITLE", "PLACE", "STARTING_POINT", "MAX_ATTENDEES", "PRICE", "START_DATE", "CANCELLED" -> sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                    case "EMPLOYEE_ID" -> {
                        if (!entry.getValue().equals("-1")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                        }
                    }
                    case "TOPIC" -> {
                        if (!entry.getValue().equals("TODAS")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" and ");
                        }
                    }
                }
            }
        }
        if (sb.length() > sbInitialLength) {
            sb.delete(sb.length() - 5, sb.length());
            SQL = sb.toString();
        }
        if (SQL != null) {
            em = HibernateUtil.getSessionFactory().createEntityManager();
            List tours = null;
            try {
                Query query = em.createQuery(SQL);
                tours = query.getResultList();
            } catch (Exception e) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            }
            if (tours != null) {
                return new ArrayList<Tour>(tours);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public double getTourTotalRaised(Tour tour) {
        if (tour.getClients() != null) {
            return tour.getPrice() * tour.getClients().size();
        }
        return 0.0;
    }

    /*BONUS*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Bonus> getAllBonussesAchievedThisYearBy(Client client) {
        if (client.getBonuses() != null) {
            return new ArrayList<>(client.getBonuses());
        }
        return new ArrayList<>();
    }

    /*CANCELLATION_REGISTRATION*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<String> getEmployeeRegistrationCancellation(Employee employee) {
        ArrayList<String> data = new ArrayList<>();
        for (EmployeeRegistrationCancellation employeeRegistrationCancellation : employee.getEmployeeRegistrationCancellations()) {
            String startDate = String.valueOf(employeeRegistrationCancellation.getStartDate());
            String endDate = String.valueOf(employeeRegistrationCancellation.getEndDate());
            String row = "Fecha contratación " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
            data.add(row);
        }
        return data;
    }

    @Override
    public ArrayList<String> getClientRegistrationsCancellations(Client client) {
        ArrayList<String> data = new ArrayList<>();
        for (ClientRegistrationCancelation clientRegistrationCancelation : client.getClientRegistrationCancelations()) {
            String startDate = String.valueOf(clientRegistrationCancelation.getStartDate());
            String endDate = String.valueOf(clientRegistrationCancelation.getEndDate());
            String row = "Fecha inscripción " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
            data.add(row);
        }
        return data;
    }

    /*EMPLOYEE_CLOCK_IN_CLOCK_OUT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public Integer logEmployeeClockIn(Employee employee) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            EmployeeClockInClockOut employeeClockInClockOut = new EmployeeClockInClockOut(employee, Timestamp.from(Instant.now()), null);
            em.persist(employeeClockInClockOut);
            em.getTransaction().commit();
            return employeeClockInClockOut.getId();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return null;
    }

    @Override
    public void logEmployeeClockOut(int sessionId) {
        em = HibernateUtil.getSessionFactory().createEntityManager();
        EmployeeClockInClockOut employeeClockInClockOut;
        try {
            em.getTransaction().begin();
            employeeClockInClockOut = em.find(model.EmployeeClockInClockOut.class, sessionId);
            employeeClockInClockOut.setEndDate(Timestamp.from(Instant.now()));
            em.persist(employeeClockInClockOut);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Override
    public ArrayList<String> getEmployeeClockInsClockOuts(Employee employee) {
        ArrayList<String> data = new ArrayList<>();
        for (EmployeeClockInClockOut employeeClockInClockOut : employee.getEmployeeClockInClockOuts()) {
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
        ArrayList<String> data = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            String name = databaseMetaData.getDatabaseProductName();
            String driver = databaseMetaData.getDriverName();
            String url = databaseMetaData.getURL();
            String user = databaseMetaData.getUserName();
            data.add("INFORMACIÓN SOBRE LA BASE DE DATOS");
            data.add("========================================================");
            data.add("Nombre :  " + name);
            data.add("Driver :  " + driver);
            data.add("URL    :  " + url);
            data.add("Usuario:  " + user);
            data.add("========================================================");
            data.add("\n");
            data.add("\n");
            ResultSet rs = databaseMetaData.getTables(null, "agenciasturisticas", null, null);
            while (rs.next()) {
                String catalog = rs.getString(1);
                String schema = rs.getString(2);
                String table = rs.getString(3);
                String type = rs.getString(4);
                String tableInfo = (type + "  -> " + " { " + "Nombre  :  " + table + " } " + " { Esquema :  " + schema + " } " + " { Clave Primaria  :  ");
                try (ResultSet rs2 = databaseMetaData.getPrimaryKeys(catalog, schema, table)) {
                    if (rs2.next()) {
                        tableInfo = tableInfo.concat(rs2.getString("COLUMN_NAME").concat(" }"));
                    }
                }
                data.add(tableInfo);
                String headers = String.format("|%s %-30s | %s %-30s | %s %-30s | %s %-30s | ", "", "Columna", "", "Tipo", "", "Tamaño", "", "Nulo");
                String separator = "+" + String.join("", Collections.nCopies(headers.length() - 3, "-")) + "+";
                data.add(separator);
                data.add(headers);
                data.add(separator);
                ResultSet rs3 = databaseMetaData.getColumns(null, "agenciasturisticas", table, null);
                while (rs3.next()) {
                    String columnName = rs3.getString("COLUMN_NAME");
                    String columnType = rs3.getString("TYPE_NAME");
                    String columnSize = rs3.getString("COLUMN_SIZE");
                    String is_nullable = rs3.getString("IS_NULLABLE");
                    String output = String.format("|%s %-30s | %s %-30s | %s %-30s | %s %-30s | ", "", columnName, "", columnType, "", columnSize, "", is_nullable);
                    data.add(output);
                }
                data.add(separator);
                data.add("\n");
                data.add("\n");
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return data;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}