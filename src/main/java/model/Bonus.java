package model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "BONUS")
public class Bonus implements Comparable<Bonus> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BONUS_ID")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;
    @Column(name = "BONUS_TYPE")
    private int NumericalBonusType;
    private BonusType bonusType;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ATTAINTMENT_DATE")
    private LocalDate attaintmentDate;

    public Bonus() {
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

    public int getNumericalBonusType() {
        return NumericalBonusType;
    }

    public void setNumericalBonusType(int numericalBonusType) {
        NumericalBonusType = numericalBonusType;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public void setBonusType(BonusType bonusType) {
        this.bonusType = bonusType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAttaintmentDate() {
        return attaintmentDate;
    }

    public void setAttaintmentDate(LocalDate attaintmentDate) {
        this.attaintmentDate = attaintmentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bonus bonus = (Bonus) o;
        return id == bonus.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Bonus b) {
        return this.getAttaintmentDate().compareTo(b.attaintmentDate);
    }

    @Override
    public String toString() {
        return "Bonus{" +
                "id=" + id +
                ", client=" + client +
                ", NumericalBonusType=" + NumericalBonusType +
                ", bonusType=" + bonusType +
                ", description='" + description + '\'' +
                ", attaintmentDate=" + attaintmentDate +
                '}';
    }

    public enum BonusType {
        ONE_STAR("5% de descuento", 5),
        TWO_STARS("10% de descuento", 10),
        THREE_STARS("15% de descuento", 15),
        FOUR_STARS("20% de descuento", 20),
        FIVE_STARS("25% de descuento", 25);

        private final String description;
        private final int discount;

        BonusType(String description, int discount) {
            this.description = description;
            this.discount = discount;
        }

        public String getDescription() {
            return description;
        }

        public int getDiscount() {
            return discount;
        }
    }
}