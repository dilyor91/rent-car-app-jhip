package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import uz.carapp.rentcarapp.domain.enumeration.FieldTypeEnum;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.Param} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParamDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private FieldTypeEnum fieldType;

    private Boolean status;

    private Boolean isDefault;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParamDTO)) {
            return false;
        }

        ParamDTO paramDTO = (ParamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paramDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParamDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", fieldType='" + getFieldType() + "'" +
            ", status='" + getStatus() + "'" +
            ", isDefault='" + getIsDefault() + "'" +
            "}";
    }
}
