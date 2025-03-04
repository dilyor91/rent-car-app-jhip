package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.MerchantDocument} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MerchantDocumentDTO implements Serializable {

    private Long id;

    private MerchantDTO merchant;

    private DocumentDTO document;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MerchantDTO getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantDTO merchant) {
        this.merchant = merchant;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MerchantDocumentDTO)) {
            return false;
        }

        MerchantDocumentDTO merchantDocumentDTO = (MerchantDocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, merchantDocumentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MerchantDocumentDTO{" +
            "id=" + getId() +
            ", merchant=" + getMerchant() +
            ", document=" + getDocument() +
            "}";
    }
}
