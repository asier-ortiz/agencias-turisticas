package controller;

import model.*;

import java.util.ArrayList;
import java.util.Map;

public interface DataBase {

    /*EMPLOYEE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    Employee login(String email, String password);

    boolean insertEmployee(Employee employee);

    Employee getEmployee(int id);

    ArrayList<Employee> getAllEmployeesWhereActiveIs(boolean active);

    ArrayList<Employee> getFilteredEmployees(Map<String, String> map);

    boolean updateEmployee(Employee employee);

    boolean dismissEmployee(int id);

    boolean reemployEmployee(int id);

    /*CLIENT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    boolean insertClient(Client client);

    boolean updateClient(Client client);

    boolean dismissClient(int id);

    boolean reenrollClient(int id);

    boolean singUpClientForTour(Client client, Tour tour);

    boolean unsubscribeClientForTour(Client client, Tour tour);

    ArrayList<Client> getAllClientsWhereActiveIs(boolean active);

    ArrayList<Client> getFilteredClients(Map<String, String> map);

    ArrayList<Client> getAllAttendeesForTour(Tour tour);

    double getClientAnnualExpense(Client client);

    int getClientAnnualDiscountPercentage(Client client);

    /*TOUR*/
    /*----------------------------------------------------------------------------------------------------------------*/

    boolean insertTour(Tour tour);

    boolean updateTour(Tour tour);

    boolean cancelTour(int id);

    boolean reactivateTour(int id);

    ArrayList<Tour> getAllActiveTours();

    ArrayList<Tour> getAllToursCreatedBy(Employee employee);

    ArrayList<Tour> getAllToursAttendedBy(Client client);

    ArrayList<Tour> getFilteredTours(Map<String, String> map);

    double getTourTotalRaised(Tour tour);

    /*BONUS*/
    /*----------------------------------------------------------------------------------------------------------------*/

    ArrayList<Bonus> getAllBonussesAchievedThisYearBy(Client client);

    /*CANCELLATION_REGISTRATION*/
    /*----------------------------------------------------------------------------------------------------------------*/

    ArrayList<String> getEmployeeRegistrationCancellation(Employee employee);

    ArrayList<String> getClientRegistrationsCancellations(Client client);

    /*EMPLOYEE_CLOCK_IN_CLOCK_OUT*/
    /*----------------------------------------------------------------------------------------------------------------*/

    Integer logEmployeeClockIn(Employee employee);

    void logEmployeeClockOut(int sessionId);

    ArrayList<String> getEmployeeClockInsClockOuts(Employee employee);

    /*DATABASE*/
    /*----------------------------------------------------------------------------------------------------------------*/

    ArrayList<String> getDataBaseMetaData();

    /*----------------------------------------------------------------------------------------------------------------*/
}
