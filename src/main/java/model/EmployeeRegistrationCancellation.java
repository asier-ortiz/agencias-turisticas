package model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "EMPLOYEE_REGISTRATION_CANCELLATION")
public class EmployeeRegistrationCancellation implements Comparable<EmployeeRegistrationCancellation> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REGISTRATION_CANCELLATION_ID")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @Column(name = "END_DATE")
    private LocalDate endDate;

    public EmployeeRegistrationCancellation() {
    }

    public EmployeeRegistrationCancellation(int id, Employee employee, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EmployeeRegistrationCancellation(Employee employee, LocalDate startDate, LocalDate endDate) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeRegistrationCancellation that = (EmployeeRegistrationCancellation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(EmployeeRegistrationCancellation e) {
        return this.getStartDate().compareTo(e.startDate);
    }

    @Override
    public String toString() {
        return "EmployeeRegistrationCancellation{" +
                "id=" + id +
                ", employee=" + employee +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}