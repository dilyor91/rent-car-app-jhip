package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.DocAttachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocAttachmentDTO implements Serializable {

    private Long id;

    private DocumentDTO document;

    private AttachmentDTO attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

    public AttachmentDTO getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentDTO attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocAttachmentDTO)) {
            return false;
        }

        DocAttachmentDTO docAttachmentDTO = (DocAttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, docAttachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocAttachmentDTO{" +
            "id=" + getId() +
            ", document=" + getDocument() +
            ", attachment=" + getAttachment() +
            "}";
    }
}
