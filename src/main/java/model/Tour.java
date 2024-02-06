package model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TOUR")
public class Tour implements Comparable<Tour> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TOUR_ID")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TOPIC")
    private String topic;
    @Column(name = "PLACE")
    private String place;
    @Column(name = "STARTING_POINT")
    private String startingPoint;
    @Column(name = "MAX_ATTENDEES")
    private int maxAttendees;
    @Column(name = "PRICE")
    private double price;
    @Column(name = "START_DATE")
    private LocalDateTime startDate;
    @Column(name = "CANCELLED")
    private boolean cancelled;
    @ManyToMany(mappedBy = "tours", fetch = FetchType.EAGER)
    private Set<Client> clients;

    public Tour() {
    }

    public Tour(int id, Employee employee, String title, String description, String topic, String place, String startingPoint, int maxAttendees, double price, LocalDateTime startDate, boolean cancelled) {
        this.id = id;
        this.employee = employee;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.place = place;
        this.startingPoint = startingPoint;
        this.maxAttendees = maxAttendees;
        this.price = price;
        this.startDate = startDate;
        this.cancelled = cancelled;
    }

    public Tour(Employee employee, String title, String description, String topic, String place, String startingPoint, int maxAttendees, double price, LocalDateTime startDate) {
        this.employee = employee;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.place = place;
        this.startingPoint = startingPoint;
        this.maxAttendees = maxAttendees;
        this.price = price;
        this.startDate = startDate;
        cancelled = false;
        clients = new HashSet<>();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(HashSet<Client> clients) {
        this.clients = clients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tour tour = (Tour) o;
        return id == tour.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Tour t) {
        return t.getStartDate().compareTo(this.getStartDate());
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", employee=" + employee +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", topic='" + topic + '\'' +
                ", place='" + place + '\'' +
                ", startingPoint='" + startingPoint + '\'' +
                ", maxAttendees=" + maxAttendees +
                ", price=" + price +
                ", startDate=" + startDate +
                ", cancelled=" + cancelled +
                ", clients=" + clients +
                '}';
    }
}