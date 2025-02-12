package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.ParamValue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParamValueDTO implements Serializable {

    private Long id;

    private String name;

    private Boolean status;

    private ParamDTO param;

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

    public ParamDTO getParam() {
        return param;
    }

    public void setParam(ParamDTO param) {
        this.param = param;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParamValueDTO)) {
            return false;
        }

        ParamValueDTO paramValueDTO = (ParamValueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paramValueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParamValueDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", param=" + getParam() +
            "}";
    }
}
