package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarAttachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarAttachmentDTO implements Serializable {

    private Long id;

    private Boolean isMain;

    private CarDTO car;

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

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
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
        if (!(o instanceof CarAttachmentDTO)) {
            return false;
        }

        CarAttachmentDTO carAttachmentDTO = (CarAttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carAttachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarAttachmentDTO{" +
            "id=" + getId() +
            ", isMain='" + getIsMain() + "'" +
            ", car=" + getCar() +
            ", attachment=" + getAttachment() +
            "}";
    }
}
