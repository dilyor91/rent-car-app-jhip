package uz.carapp.rentcarapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Car.
 */
@Entity
@Table(name = "car")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "car")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "state_number_plate")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer stateNumberPlate;

    @Column(name = "deposit")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer deposit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "brand" }, allowSetters = true)
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "merchant" }, allowSetters = true)
    private MerchantBranch merchantBranch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Car id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStateNumberPlate() {
        return this.stateNumberPlate;
    }

    public Car stateNumberPlate(Integer stateNumberPlate) {
        this.setStateNumberPlate(stateNumberPlate);
        return this;
    }

    public void setStateNumberPlate(Integer stateNumberPlate) {
        this.stateNumberPlate = stateNumberPlate;
    }

    public Integer getDeposit() {
        return this.deposit;
    }

    public Car deposit(Integer deposit) {
        this.setDeposit(deposit);
        return this;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Car model(Model model) {
        this.setModel(model);
        return this;
    }

    public Merchant getMerchant() {
        return this.merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Car merchant(Merchant merchant) {
        this.setMerchant(merchant);
        return this;
    }

    public MerchantBranch getMerchantBranch() {
        return this.merchantBranch;
    }

    public void setMerchantBranch(MerchantBranch merchantBranch) {
        this.merchantBranch = merchantBranch;
    }

    public Car merchantBranch(MerchantBranch merchantBranch) {
        this.setMerchantBranch(merchantBranch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        return getId() != null && getId().equals(((Car) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Car{" +
            "id=" + getId() +
            ", stateNumberPlate=" + getStateNumberPlate() +
            ", deposit=" + getDeposit() +
            "}";
    }
}
