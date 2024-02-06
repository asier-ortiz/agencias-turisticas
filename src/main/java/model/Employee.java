package model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "EMPLOYEE")
public class Employee implements Comparable<Employee> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private int id;
    @Column(name = "DNI")
    private String dni;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FIRST_SURNAME")
    private String firstSurname;
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;
    @Column(name = "NATIONALITY")
    private String nationality;
    @Column(name = "ROLE")
    private String role;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "ACTIVE")
    private boolean active;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
    private Set<Tour> tours;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<EmployeeRegistrationCancellation> employeeRegistrationCancellations;
    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<EmployeeClockInClockOut> employeeClockInClockOuts;

    public Employee() {
    }

    public Employee(int id) {
        this.id = id;
    }

    public Employee(int id, String name, String firstSurname) {
        this.id = id;
        this.name = name;
        this.firstSurname = firstSurname;
    }

    public Employee(int id, String dni, String name, String firstSurname, LocalDate birthDate, String nationality, String role, String email, String password, boolean active) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.firstSurname = firstSurname;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.role = role;
        this.email = email;
        this.password = password;
        this.active = active;
    }

    public Employee(String dni, String name, String firstSurname, LocalDate birthDate, String nationality, String role, String email, String password) {
        this.dni = dni;
        this.name = name;
        this.firstSurname = firstSurname;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.role = role;
        this.email = email;
        this.password = password;
        active = true;
        tours = new HashSet<>();
        employeeRegistrationCancellations = new HashSet<>();
        employeeClockInClockOuts = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Tour> getTours() {
        return tours;
    }

    public void setTours(Set<Tour> tours) {
        this.tours = tours;
    }

    public Set<EmployeeRegistrationCancellation> getEmployeeRegistrationCancellations() {
        return employeeRegistrationCancellations;
    }

    public void setEmployeeRegistrationCancellations(Set<EmployeeRegistrationCancellation> employeeRegistrationCancellations) {
        this.employeeRegistrationCancellations = employeeRegistrationCancellations;
    }

    public Set<EmployeeClockInClockOut> getEmployeeClockInClockOuts() {
        return employeeClockInClockOuts;
    }

    public void setEmployeeClockInClockOuts(Set<EmployeeClockInClockOut> employeeClockInClockOuts) {
        this.employeeClockInClockOuts = employeeClockInClockOuts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Employee e) {
        return this.getName().compareTo(e.getName());
    }

    @Override
    public String toString() {
        if (id != -1) {
            return "ID {" + id + "} " + name + " " + firstSurname;
        } else {
            return name;
        }
    }
}