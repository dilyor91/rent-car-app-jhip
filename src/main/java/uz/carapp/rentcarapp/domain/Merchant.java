package uz.carapp.rentcarapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Merchant.
 */
@Entity
@Table(name = "merchant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "merchant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "company_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String companyName;

    @Column(name = "brand_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String brandName;

    @Column(name = "inn")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String inn;

    @Column(name = "owner")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String owner;

    @Column(name = "phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phone;

    @Column(name = "address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Merchant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public Merchant companyName(String companyName) {
        this.setCompanyName(companyName);
        return this;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBrandName() {
        return this.brandName;
    }

    public Merchant brandName(String brandName) {
        this.setBrandName(brandName);
        return this;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getInn() {
        return this.inn;
    }

    public Merchant inn(String inn) {
        this.setInn(inn);
        return this;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getOwner() {
        return this.owner;
    }

    public Merchant owner(String owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return this.phone;
    }

    public Merchant phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public Merchant address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Merchant)) {
            return false;
        }
        return getId() != null && getId().equals(((Merchant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Merchant{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", brandName='" + getBrandName() + "'" +
            ", inn='" + getInn() + "'" +
            ", owner='" + getOwner() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
