package model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CLIENT")
public class Client implements Comparable<Client> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CLIENT_ID")
    private int id;
    @Column(name = "DNI")
    private String dni;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FIRST_SURNAME")
    private String firstSurname;
    @Column(name = "SECOND_SURNAME")
    private String secondSurname;
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;
    @Column(name = "PROFESSION")
    private String profession;
    @Column(name = "ACTIVE")
    private boolean active;
    @ManyToMany(targetEntity = Tour.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "CLIENT_TOUR",
            joinColumns = {@JoinColumn(name = "CLIENT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TOUR_ID")}
    )
    private Set<Tour> tours;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ClientRegistrationCancelation> clientRegistrationCancelations;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Bonus> bonuses;


    public Client() {
    }

    public Client(int id, String dni, String name, String firstSurname, String secondSurname, LocalDate birthDate, String profession, boolean active) {
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.birthDate = birthDate;
        this.profession = profession;
        this.active = active;
    }

    public Client(String dni, String name, String firstSurname, String secondSurname, LocalDate birthDate, String profession) {
        this.dni = dni;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.birthDate = birthDate;
        this.profession = profession;
        active = true;
        tours = new HashSet<>();
        clientRegistrationCancelations = new HashSet<>();
        bonuses = new HashSet<>();
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

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public Set<ClientRegistrationCancelation> getClientRegistrationCancelations() {
        return clientRegistrationCancelations;
    }

    public void setClientRegistrationCancelations(Set<ClientRegistrationCancelation> clientRegistrationCancelations) {
        this.clientRegistrationCancelations = clientRegistrationCancelations;
    }

    public Set<Bonus> getBonuses() {
        return bonuses;
    }

    public void setBonuses(Set<Bonus> bonuses) {
        this.bonuses = bonuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Client c) {
        return this.getName().compareTo(c.getName());
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", firstSurname='" + firstSurname + '\'' +
                ", secondSurname='" + secondSurname + '\'' +
                ", birthDate=" + birthDate +
                ", profession='" + profession + '\'' +
                ", active=" + active +
                '}';
    }
}