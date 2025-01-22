package uz.carapp.rentcarapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarBody} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarBodyDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Boolean status;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarBodyDTO)) {
            return false;
        }

        CarBodyDTO carBodyDTO = (CarBodyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carBodyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarBodyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
