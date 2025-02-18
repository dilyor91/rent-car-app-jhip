package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarTemplateDTO implements Serializable {

    private Long id;

    private Boolean status;

    private ModelDTO model;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ModelDTO getModel() {
        return model;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarTemplateDTO)) {
            return false;
        }

        CarTemplateDTO carTemplateDTO = (CarTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarTemplateDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", model=" + getModel() +
            "}";
    }
}
