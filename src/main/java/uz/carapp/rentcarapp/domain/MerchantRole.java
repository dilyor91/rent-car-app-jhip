package uz.carapp.rentcarapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.carapp.rentcarapp.domain.enumeration.MerchantRoleEnum;

/**
 * A MerchantRole.
 */
@Entity
@Table(name = "merchant_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "merchantrole")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MerchantRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_role_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private MerchantRoleEnum merchantRoleType;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "merchant" }, allowSetters = true)
    private MerchantBranch merchantBranch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MerchantRole id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MerchantRoleEnum getMerchantRoleType() {
        return this.merchantRoleType;
    }

    public MerchantRole merchantRoleType(MerchantRoleEnum merchantRoleType) {
        this.setMerchantRoleType(merchantRoleType);
        return this;
    }

    public void setMerchantRoleType(MerchantRoleEnum merchantRoleType) {
        this.merchantRoleType = merchantRoleType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MerchantRole user(User user) {
        this.setUser(user);
        return this;
    }

    public Merchant getMerchant() {
        return this.merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public MerchantRole merchant(Merchant merchant) {
        this.setMerchant(merchant);
        return this;
    }

    public MerchantBranch getMerchantBranch() {
        return this.merchantBranch;
    }

    public void setMerchantBranch(MerchantBranch merchantBranch) {
        this.merchantBranch = merchantBranch;
    }

    public MerchantRole merchantBranch(MerchantBranch merchantBranch) {
        this.setMerchantBranch(merchantBranch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MerchantRole)) {
            return false;
        }
        return getId() != null && getId().equals(((MerchantRole) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MerchantRole{" +
            "id=" + getId() +
            ", merchantRoleType='" + getMerchantRoleType() + "'" +
            "}";
    }
}
