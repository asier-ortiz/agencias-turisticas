package model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "CLIENT_REGISTRATION_CANCELLATION")
public class ClientRegistrationCancelation implements Comparable<ClientRegistrationCancelation> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REGISTRATION_CANCELLATION_ID")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @Column(name = "END_DATE")
    private LocalDate endDate;

    public ClientRegistrationCancelation() {
    }

    public ClientRegistrationCancelation(int id, Client client, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ClientRegistrationCancelation(Client client, LocalDate startDate, LocalDate endDate) {
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
        ClientRegistrationCancelation that = (ClientRegistrationCancelation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ClientRegistrationCancelation c) {
        return this.getStartDate().compareTo(c.startDate);
    }

    @Override
    public String toString() {
        return "ClientRegistrationCancelation{" +
                "id=" + id +
                ", client=" + client +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}