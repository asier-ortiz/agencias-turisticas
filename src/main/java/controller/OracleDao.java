package controller;

import model.*;
import util.DBConnectionUtil;
import util.DialogManagerUtil;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class OracleDao implements DataBase {
    private static OracleDao oracleDao;

    private OracleDao() {
    }

    private synchronized static void createInstence() {
        if (oracleDao == null) {
            oracleDao = new OracleDao();
        }
    }

    public static OracleDao getInstance() {
        createInstence();
        return oracleDao;
    }

    /*EMPLOYEE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public Employee login(String email, String password) {
        Employee employee = null;
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLOYEE WHERE EMAIL=? AND PASSWORD=? AND ACTIVE=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setBoolean(3, true);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                employee = new Employee(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        LocalDate.parse(String.valueOf(rs.getDate(5))),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getBoolean(10));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return employee;
    }

    @Override
    public boolean insertEmployee(Employee employee) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO EMPLOYEE (EMPLOYEE_ID, DNI, NAME, FIRST_SURNAME, BIRTH_DATE, NATIONALITY, ROLE, EMAIL, PASSWORD)"
                            + "VALUES (EMPLOYEE_ID_SEQ.nextval,?,?,?,?,?,?,?,?)");
            ps.setString(1, employee.getDni());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getFirstSurname());
            ps.setDate(4, Date.valueOf(employee.getBirthDate()));
            ps.setString(5, employee.getNationality());
            ps.setString(6, employee.getRole());
            ps.setString(7, employee.getEmail());
            ps.setString(8, employee.getPassword());
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO EMPLOYEE_REGISTRATION_CANCELLATION (REGISTRATION_CANCELLATION_ID, EMPLOYEE_ID) VALUES (EMPLOYEE_REGISTRATION_CANCELLATION_ID_SEQ.nextval, EMPLOYEE_ID_SEQ.currval)");
            updateCount = ps.executeUpdate();
            ps2.executeUpdate();
            ps.close();
            ps2.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Empleado creado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Employee getEmployee(int id) {
        Employee employee = null;
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                employee = new Employee(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        LocalDate.parse(String.valueOf(rs.getDate(5))),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getBoolean(10));
            }
            ps.close();
            conn.close();
            return employee;
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
            return employee;
        }
    }

    @Override
    public ArrayList<Employee> getAllEmployeesWhereActiveIs(boolean active) {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLOYEE WHERE ACTIVE = ?");
            ps.setInt(1, active ? 1 : 0);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                employees.add(new Employee(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        LocalDate.parse(String.valueOf(rs.getDate(5))),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getInt(10) != 0));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public ArrayList<Employee> getFilteredEmployees(Map<String, String> map) {
        ArrayList<Employee> employees = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM EMPLOYEE WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "EMPLOYEE_ID":
                        sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" AND ");
                        break;
                    case "DNI":
                    case "NAME":
                    case "FIRST_SURNAME":
                    case "BIRTH_DATE":
                    case "NATIONALITY":
                    case "EMAIL":
                    case "ACTIVE":
                        sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
                        break;
                    case "ROLE":
                        if (!entry.getValue().equals("TODOS")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
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
            try {
                Connection conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    employees.add(new Employee(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            LocalDate.parse(String.valueOf(rs.getDate(5))),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getInt(10) != 0));
                }
                ps.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println("SQL Connection error...");
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                e.printStackTrace();
            }
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EMPLOYEE SET DNI=?,NAME=?, FIRST_SURNAME=?, BIRTH_DATE=?, NATIONALITY=?, EMAIL=?, ROLE=? WHERE EMPLOYEE_ID = ?");
            ps.setString(1, employee.getDni());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getFirstSurname());
            ps.setDate(4, Date.valueOf(employee.getBirthDate()));
            ps.setString(5, employee.getNationality());
            ps.setString(6, employee.getEmail());
            ps.setString(7, employee.getRole());
            ps.setInt(8, employee.getId());
            updateCount = ps.executeUpdate();
            ps.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Empleado editado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean dismissEmployee(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea dar de baja a este empleado?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE EMPLOYEE SET ACTIVE = ? WHERE EMPLOYEE_ID = ?");
                ps.setInt(1, 0);
                ps.setInt(2, id);
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE EMPLOYEE_REGISTRATION_CANCELLATION SET END_DATE = ? WHERE  EMPLOYEE_ID = ?");
                ps2.setDate(1, Date.valueOf(LocalDate.now()));
                ps2.setInt(2, id);
                updateCount = ps.executeUpdate();
                ps2.executeUpdate();
                ps.close();
                ps2.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Empleado dado de baja");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean reemployEmployee(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea volver a dar de alta a este empleado?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE EMPLOYEE SET ACTIVE = ? WHERE EMPLOYEE_ID = ?");
                ps.setInt(1, 1);
                ps.setInt(2, id);
                PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO EMPLOYEE_REGISTRATION_CANCELLATION (REGISTRATION_CANCELLATION_ID, EMPLOYEE_ID) VALUES (EMPLOYEE_REGISTRATION_CANCELLATION_ID_SEQ.nextval, ?)");
                ps2.setInt(1, id);
                updateCount = ps.executeUpdate();
                ps2.executeUpdate();
                ps.close();
                ps2.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Empleado dado de alta");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    /*CLIENT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean insertClient(Client client) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO CLIENT (CLIENT_ID, DNI, NAME, FIRST_SURNAME, SECOND_SURNAME, BIRTH_DATE, PROFESSION)"
                            + "VALUES (CLIENT_ID_SEQ.nextval,?,?,?,?,?,?)");
            ps.setString(1, client.getDni());
            ps.setString(2, client.getName());
            ps.setString(3, client.getFirstSurname());
            ps.setString(4, client.getSecondSurname());
            ps.setDate(5, Date.valueOf(client.getBirthDate()));
            ps.setString(6, client.getProfession());
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO CLIENT_REGISTRATION_CANCELLATION (REGISTRATION_CANCELLATION_ID, CLIENT_ID) VALUES (CLIENT_REGISTRATION_CANCELLATION_ID_SEQ.nextval, CLIENT_ID_SEQ.currval)");
            updateCount = ps.executeUpdate();
            ps2.executeUpdate();
            ps.close();
            ps2.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Cliente creado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean updateClient(Client client) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE CLIENT SET DNI=?,NAME=?, FIRST_SURNAME=?, SECOND_SURNAME=?, BIRTH_DATE=?, PROFESSION=? WHERE CLIENT_ID = ?");
            ps.setString(1, client.getDni());
            ps.setString(2, client.getName());
            ps.setString(3, client.getFirstSurname());
            ps.setString(4, client.getSecondSurname());
            ps.setDate(5, Date.valueOf(client.getBirthDate()));
            ps.setString(6, client.getProfession());
            ps.setInt(7, client.getId());
            updateCount = ps.executeUpdate();
            ps.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Cliente editado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean dismissClient(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea dar de baja a este cliente?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE CLIENT SET ACTIVE = ? WHERE CLIENT_ID = ?");
                ps.setInt(1, 0);
                ps.setInt(2, id);
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE CLIENT_REGISTRATION_CANCELLATION SET END_DATE = ? WHERE  CLIENT_ID = ?");
                ps2.setDate(1, Date.valueOf(LocalDate.now()));
                ps2.setInt(2, id);
                updateCount = ps.executeUpdate();
                ps2.executeUpdate();
                ps.close();
                ps2.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Cliente dado de baja");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean reenrollClient(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea volver a dar de alta a este cliente?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE CLIENT SET ACTIVE = ? WHERE CLIENT_ID = ?");
                ps.setInt(1, 1);
                ps.setInt(2, id);
                PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO CLIENT_REGISTRATION_CANCELLATION (REGISTRATION_CANCELLATION_ID, CLIENT_ID) VALUES (CLIENT_REGISTRATION_CANCELLATION_ID_SEQ.nextval, ?)");
                ps2.setInt(1, id);
                updateCount = ps.executeUpdate();
                ps2.executeUpdate();
                ps.close();
                ps2.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Cliente dado de alta");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean singUpClientForTour(Client client, Tour tour) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CLIENT_TOUR (CLIENT_ID, TOUR_ID) VALUES (?,?)");
            ps.setInt(1, client.getId());
            ps.setInt(2, tour.getId());
            updateCount = ps.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM CLIENT_TOUR WHERE CLIENT_ID=? AND CLIENT_TOUR.TOUR_ID IN (SELECT TOUR_ID FROM TOUR WHERE EXTRACT(YEAR FROM TOUR.START_DATE) IN (SELECT TO_CHAR(SYSDATE, 'YYYY') FROM DUAL))");
            ps2.setInt(1, client.getId());
            ResultSet rs = ps2.executeQuery();
            int attendedToursCount = 0;
            if (rs.next()) {
                attendedToursCount = rs.getInt(1);
            }
            PreparedStatement ps3 = null;
            switch (attendedToursCount) {
                case 5 -> {
                    ps3 = conn.prepareCall("INSERT INTO BONUS (BONUS_ID, CLIENT_ID, BONUS_TYPE, DESCRIPTION) VALUES (BONUS_ID_SEQ.nextval, ?, 5, ?)");
                    ps3.setInt(1, client.getId());
                    ps3.setString(2, "5% descuento");
                }
                case 10 -> {
                    ps3 = conn.prepareCall("INSERT INTO BONUS (BONUS_ID, CLIENT_ID, BONUS_TYPE, DESCRIPTION) VALUES (BONUS_ID_SEQ.nextval, ?, 10, ?)");
                    ps3.setInt(1, client.getId());
                    ps3.setString(2, "10% descuento");
                }
                case 15 -> {
                    ps3 = conn.prepareCall("INSERT INTO BONUS (BONUS_ID, CLIENT_ID, BONUS_TYPE, DESCRIPTION) VALUES (BONUS_ID_SEQ.nextval, ?, 15, ?)");
                    ps3.setInt(1, client.getId());
                    ps3.setString(2, "15% descuento");
                }
                case 20 -> {
                    ps3 = conn.prepareCall("INSERT INTO BONUS (BONUS_ID, CLIENT_ID, BONUS_TYPE, DESCRIPTION) VALUES (BONUS_ID_SEQ.nextval, ?, 20, ?)");
                    ps3.setInt(1, client.getId());
                    ps3.setString(2, "20% descuento");
                }
                case 25 -> {
                    ps3 = conn.prepareCall("INSERT INTO BONUS (BONUS_ID, CLIENT_ID, BONUS_TYPE, DESCRIPTION) VALUES (BONUS_ID_SEQ.nextval, ?, 25, ?)");
                    ps3.setInt(1, client.getId());
                    ps3.setString(2, "25% descuento");
                }
            }
            if (ps3 != null) {
                ps3.executeUpdate();
                ps3.close();
            }
            ps.close();
            ps2.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Cliente apuntado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean unsubscribeClientForTour(Client client, Tour tour) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM CLIENT_TOUR WHERE (CLIENT_ID)=? AND (TOUR_ID)=?");
            ps.setInt(1, client.getId());
            ps.setInt(2, tour.getId());
            updateCount = ps.executeUpdate();
            ps.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Cliente desapuntado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public ArrayList<Client> getAllClientsWhereActiveIs(boolean active) {
        ArrayList<Client> clients = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENT WHERE ACTIVE = ?");
            ps.setInt(1, active ? 1 : 0);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(new Client(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        LocalDate.parse(String.valueOf(rs.getDate(6))),
                        rs.getString(7),
                        rs.getInt(8) != 0));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return clients;
    }

    @Override
    public ArrayList<Client> getFilteredClients(Map<String, String> map) {
        ArrayList<Client> clients = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM CLIENT WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "CLIENT_ID" -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" AND ");
                    case "DNI", "NAME", "FIRST_SURNAME", "SECOND_SURNAME", "BIRTH_DATE", "PROFESSION", "ACTIVE" -> sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
                }
            }
        }
        if (sb.length() > sbInitialLength) {
            sb.delete(sb.length() - 5, sb.length());
            SQL = sb.toString();
        }
        if (SQL != null) {
            try {
                Connection conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    clients.add(new Client(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            LocalDate.parse(String.valueOf(rs.getDate(6))),
                            rs.getString(7),
                            rs.getInt(8) != 0));
                }
                ps.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println("SQL Connection error...");
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                e.printStackTrace();
            }
        }
        return clients;
    }

    @Override
    public ArrayList<Client> getAllAttendeesForTour(Tour tour) {
        ArrayList<Client> clients = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENT T NATURAL JOIN CLIENT_TOUR CT WHERE CT.TOUR_ID=?");
            ps.setInt(1, tour.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(new Client(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        LocalDate.parse(String.valueOf(rs.getDate(6))),
                        rs.getString(7),
                        rs.getInt(8) != 0));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return clients;
    }

    @Override
    public double getClientAnnualExpense(Client client) {
        double total = 0.0;
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT T.PRICE FROM TOUR T NATURAL JOIN CLIENT_TOUR CT WHERE CT.CLIENT_ID=? AND EXTRACT(YEAR FROM T.START_DATE) IN (SELECT TO_CHAR(SYSDATE, 'YYYY') FROM DUAL) AND T.CANCELLED = 0");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble(1);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return total;
    }

    @Override
    public int getClientAnnualDiscountPercentage(Client client) {
        int percentage = 0;
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(BONUS_TYPE) FROM BONUS WHERE CLIENT_ID=? AND EXTRACT(YEAR FROM ATTAINTMENT_DATE) IN (SELECT TO_CHAR(SYSDATE, 'YYYY') FROM  DUAL)");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                percentage = rs.getInt(1);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return percentage;
    }

    /*TOUR*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean insertTour(Tour tour) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO TOUR (TOUR_ID, EMPLOYEE_ID, TITLE, DESCRIPTION, TOPIC, PLACE, STARTING_POINT, MAX_ATTENDEES, PRICE, START_DATE, CANCELLED)"
                            + "VALUES (TOUR_ID_SEQ.nextval,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, tour.getEmployee().getId());
            ps.setString(2, tour.getTitle());
            ps.setString(3, tour.getDescription());
            ps.setString(4, tour.getTopic());
            ps.setString(5, tour.getPlace());
            ps.setString(6, tour.getStartingPoint());
            ps.setInt(7, tour.getMaxAttendees());
            ps.setDouble(8, tour.getPrice());
            ps.setTimestamp(9, Timestamp.valueOf(tour.getStartDate()));
            ps.setInt(10, tour.isCancelled() ? 1 : 0);
            updateCount = ps.executeUpdate();
            ps.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Tour creado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean updateTour(Tour tour) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE TOUR SET EMPLOYEE_ID=?, TITLE=?, DESCRIPTION=?, TOPIC=?, PLACE=?, STARTING_POINT=?, MAX_ATTENDEES=?, PRICE=?, START_DATE=?, CANCELLED=? WHERE TOUR_ID = ?");
            ps.setInt(1, tour.getEmployee().getId());
            ps.setString(2, tour.getTitle());
            ps.setString(3, tour.getDescription());
            ps.setString(4, tour.getTopic());
            ps.setString(5, tour.getPlace());
            ps.setString(6, tour.getStartingPoint());
            ps.setInt(7, tour.getMaxAttendees());
            ps.setDouble(8, tour.getPrice());
            ps.setTimestamp(9, Timestamp.valueOf(tour.getStartDate()));
            ps.setBoolean(10, tour.isCancelled());
            ps.setInt(11, tour.getId());
            updateCount = ps.executeUpdate();
            ps.close();
            conn.close();
            if (updateCount == 1) {
                DialogManagerUtil.showInfoDialog("Tour editado");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean cancelTour(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea cancelar esta visita?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE TOUR SET CANCELLED = ? WHERE TOUR_ID = ?");
                ps.setInt(1, 1);
                ps.setInt(2, id);
                updateCount = ps.executeUpdate();
                ps.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Visita cancelada");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean reactivateTour(int id) {
        Connection conn = null;
        int updateCount;
        Object[] options = {"Si", "No"};
        int reply = DialogManagerUtil.showOptionDialog(
                options,
                "¿Desea reactivar esta visita?",
                "¡Atención!",
                JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                1);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement("UPDATE TOUR SET CANCELLED = ? WHERE TOUR_ID = ?");
                ps.setInt(1, 0);
                ps.setInt(2, id);
                updateCount = ps.executeUpdate();
                ps.close();
                conn.close();
                if (updateCount == 1) {
                    DialogManagerUtil.showInfoDialog("Visita reactivada");
                    return true;
                }
            } catch (SQLException ex) {
                System.err.println("SQL Connection error...");
                ex.printStackTrace();
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("SQL Rollback error...");
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<Tour> getAllActiveTours() {
        ArrayList<Tour> tours = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TOUR WHERE CANCELLED = 0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tours.add(new Tour(rs.getInt(1),
                        new Employee(rs.getInt(2)),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getTimestamp(10).toLocalDateTime(),
                        rs.getBoolean(11)));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        Collections.sort(tours);
        return tours;
    }

    @Override
    public ArrayList<Tour> getAllToursCreatedBy(Employee employee) {
        ArrayList<Tour> tours = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TOUR WHERE EMPLOYEE_ID = ?");
            ps.setInt(1, employee.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tours.add(new Tour(rs.getInt(1),
                        new Employee(rs.getInt(2)),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getTimestamp(10).toLocalDateTime(),
                        rs.getBoolean(11)));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return tours;
    }

    @Override
    public ArrayList<Tour> getAllToursAttendedBy(Client client) {
        ArrayList<Tour> tours = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TOUR T NATURAL JOIN CLIENT_TOUR CT WHERE CT.CLIENT_ID=?");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tours.add(new Tour(rs.getInt(1),
                        new Employee(rs.getInt(2)),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getTimestamp(10).toLocalDateTime(),
                        rs.getBoolean(11)));
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return tours;
    }

    @Override
    public ArrayList<Tour> getFilteredTours(Map<String, String> map) {
        ArrayList<Tour> tours = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM TOUR WHERE ");
        int sbInitialLength = sb.length();
        String SQL = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                switch (entry.getKey()) {
                    case "TOUR_ID" -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append(" AND ");
                    case "TITLE", "PLACE", "STARTING_POINT", "MAX_ATTENDEES", "PRICE", "START_DATE", "CANCELLED" -> sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
                    case "EMPLOYEE_ID" -> {
                        if (!entry.getValue().equals("-1")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
                        }
                    }
                    case "TOPIC" -> {
                        if (!entry.getValue().equals("TODAS")) {
                            sb.append(entry.getKey()).append(" = ").append("'").append(entry.getValue()).append("'").append(" AND ");
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
            try {
                Connection conn = DBConnectionUtil.getConexion();
                PreparedStatement ps = conn.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tours.add(new Tour(rs.getInt(1),
                            new Employee(rs.getInt(2)),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getInt(8),
                            rs.getDouble(9),
                            rs.getTimestamp(10).toLocalDateTime(),
                            rs.getBoolean(11)));
                }
                ps.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println("SQL Connection error...");
                DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
                e.printStackTrace();
            }
        }
        return tours;
    }

    @Override
    public double getTourTotalRaised(Tour tour) {
        double amount = 0.0;
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement(" SELECT ((SELECT PRICE FROM TOUR WHERE TOUR_ID = ?) * (SELECT COUNT(*) FROM CLIENT_TOUR WHERE TOUR_ID = ?)) FROM DUAL ");
            ps.setInt(1, tour.getId());
            ps.setInt(2, tour.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                amount = rs.getDouble(1);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return amount;
    }

    /*BONUS*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Bonus> getAllBonussesAchievedThisYearBy(Client client) {
        ArrayList<Bonus> bonuses = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM BONUS WHERE CLIENT_ID=? AND EXTRACT(YEAR FROM ATTAINTMENT_DATE) IN (SELECT TO_CHAR(SYSDATE, 'YYYY') FROM  DUAL)");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bonus bonus = new Bonus();
                bonus.setId(rs.getInt(1));
                bonus.setClient(client);
                switch (rs.getInt(3)) {
                    case 5 -> bonus.setBonusType(Bonus.BonusType.ONE_STAR);
                    case 10 -> bonus.setBonusType(Bonus.BonusType.TWO_STARS);
                    case 15 -> bonus.setBonusType(Bonus.BonusType.THREE_STARS);
                    case 20 -> bonus.setBonusType(Bonus.BonusType.FOUR_STARS);
                    case 25 -> bonus.setBonusType(Bonus.BonusType.FIVE_STARS);
                }
                bonus.setDescription(rs.getString(4));
                bonus.setAttaintmentDate(LocalDate.parse(String.valueOf(rs.getDate(5))));
                bonuses.add(bonus);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return bonuses;
    }

    /*CANCELLATION_REGISTRATION*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<String> getEmployeeRegistrationCancellation(Employee employee) {
        ArrayList<String> data = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLOYEE_REGISTRATION_CANCELLATION WHERE EMPLOYEE_ID = ?");
            ps.setInt(1, employee.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String startDate = String.valueOf(rs.getDate(3));
                String endDate = String.valueOf(rs.getDate(4));
                String row = "Fecha contratación " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
                data.add(row);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public ArrayList<String> getClientRegistrationsCancellations(Client client) {
        ArrayList<String> data = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENT_REGISTRATION_CANCELLATION WHERE CLIENT_ID = ?");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String startDate = String.valueOf(rs.getDate(3));
                String endDate = String.valueOf(rs.getDate(4));
                String row = "Fecha inscripción " + "(" + startDate + ")" + " || " + "Fecha baja " + "(" + endDate + ")";
                data.add(row);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
        }
        return data;
    }

    /*EMPLOYEE_CLOCK_IN_CLOCK_OUT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public Integer logEmployeeClockIn(Employee employee) {
        Connection conn = null;
        int updateCount;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO EMPLOYEE_CLOCK_IN_CLOCK_OUT (SESSION_ID, EMPLOYEE_ID) " +
                    "VALUES (EMPLOYEE_CLOCK_IN_CLOCK_OUT_ID_SEQ.nextval, ?)");
            ps.setInt(1, employee.getId());
            updateCount = ps.executeUpdate();
            ps.close();
            if (updateCount == 1) {
                PreparedStatement ps2 = conn.prepareStatement("SELECT EMPLOYEE_CLOCK_IN_CLOCK_OUT_ID_SEQ.currval FROM DUAL");
                ResultSet rs = ps2.executeQuery();
                Integer lastId = null;
                if (rs.next()) {
                    lastId = rs.getInt(1);
                }
                ps2.close();
                conn.close();
                return lastId;
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void logEmployeeClockOut(int sessionId) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("UPDATE EMPLOYEE_CLOCK_IN_CLOCK_OUT SET END_DATE=CURRENT_TIMESTAMP WHERE SESSION_ID=?");
            ps.setInt(1, sessionId);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println("SQL Connection error...");
            ex.printStackTrace();
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("SQL Rollback error...");
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public ArrayList<String> getEmployeeClockInsClockOuts(Employee employee) {
        ArrayList<String> data = new ArrayList<>();
        try {
            Connection conn = DBConnectionUtil.getConexion();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM EMPLOYEE_CLOCK_IN_CLOCK_OUT WHERE EMPLOYEE_ID = ?");
            ps.setInt(1, employee.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String startDate = String.valueOf(rs.getTimestamp(3));
                String endDate = String.valueOf(rs.getTimestamp(4));
                String row = "Entrada " + "(" + startDate + ")" + " || " + "Salida " + "(" + endDate + ")";
                data.add(row);
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Connection error...");
            DialogManagerUtil.showErrorDialog("Error en la conexión a la base de datos...");
            e.printStackTrace();
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
            ResultSet rs = databaseMetaData.getTables(null, "ASIER", null, null);
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
                ResultSet rs3 = databaseMetaData.getColumns(null, "ASIER", table, null);
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