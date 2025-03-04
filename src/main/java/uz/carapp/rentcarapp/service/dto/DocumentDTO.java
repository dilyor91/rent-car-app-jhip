package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import uz.carapp.rentcarapp.domain.enumeration.DocTypeEnum;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.Document} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentDTO implements Serializable {

    private Long id;

    private String name;

    private DocTypeEnum docType;

    private Instant givenDate;

    private Instant issuedDate;

    private Boolean docStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocTypeEnum getDocType() {
        return docType;
    }

    public void setDocType(DocTypeEnum docType) {
        this.docType = docType;
    }

    public Instant getGivenDate() {
        return givenDate;
    }

    public void setGivenDate(Instant givenDate) {
        this.givenDate = givenDate;
    }

    public Instant getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Instant issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Boolean getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(Boolean docStatus) {
        this.docStatus = docStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentDTO)) {
            return false;
        }

        DocumentDTO documentDTO = (DocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, documentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", docType='" + getDocType() + "'" +
            ", givenDate='" + getGivenDate() + "'" +
            ", issuedDate='" + getIssuedDate() + "'" +
            ", docStatus='" + getDocStatus() + "'" +
            "}";
    }
}
