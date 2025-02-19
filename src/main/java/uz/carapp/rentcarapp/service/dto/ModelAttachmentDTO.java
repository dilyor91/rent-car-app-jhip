package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.ModelAttachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModelAttachmentDTO implements Serializable {

    private Long id;

    private Boolean isMain;

    private ModelDTO model;

    private AttachmentDTO attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

    public ModelDTO getModel() {
        return model;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
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
        if (!(o instanceof ModelAttachmentDTO)) {
            return false;
        }

        ModelAttachmentDTO modelAttachmentDTO = (ModelAttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, modelAttachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModelAttachmentDTO{" +
            "id=" + getId() +
            ", isMain='" + getIsMain() + "'" +
            ", model=" + getModel() +
            ", attachment=" + getAttachment() +
            "}";
    }
}
