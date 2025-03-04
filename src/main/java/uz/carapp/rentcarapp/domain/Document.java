package uz.carapp.rentcarapp.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.carapp.rentcarapp.domain.enumeration.DocTypeEnum;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "document")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private DocTypeEnum docType;

    @Column(name = "given_date")
    private Instant givenDate;

    @Column(name = "issued_date")
    private Instant issuedDate;

    @Column(name = "doc_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean docStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Document id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Document name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocTypeEnum getDocType() {
        return this.docType;
    }

    public Document docType(DocTypeEnum docType) {
        this.setDocType(docType);
        return this;
    }

    public void setDocType(DocTypeEnum docType) {
        this.docType = docType;
    }

    public Instant getGivenDate() {
        return this.givenDate;
    }

    public Document givenDate(Instant givenDate) {
        this.setGivenDate(givenDate);
        return this;
    }

    public void setGivenDate(Instant givenDate) {
        this.givenDate = givenDate;
    }

    public Instant getIssuedDate() {
        return this.issuedDate;
    }

    public Document issuedDate(Instant issuedDate) {
        this.setIssuedDate(issuedDate);
        return this;
    }

    public void setIssuedDate(Instant issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Boolean getDocStatus() {
        return this.docStatus;
    }

    public Document docStatus(Boolean docStatus) {
        this.setDocStatus(docStatus);
        return this;
    }

    public void setDocStatus(Boolean docStatus) {
        this.docStatus = docStatus;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        return getId() != null && getId().equals(((Document) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Document{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", docType='" + getDocType() + "'" +
            ", givenDate='" + getGivenDate() + "'" +
            ", issuedDate='" + getIssuedDate() + "'" +
            ", docStatus='" + getDocStatus() + "'" +
            "}";
    }
}
