package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import uz.carapp.rentcarapp.domain.enumeration.LanguageEnum;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.Translation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TranslationDTO implements Serializable {

    private Long id;

    private String entityType;

    private Long entityId;

    private LanguageEnum lang;

    private String value;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LanguageEnum getLang() {
        return lang;
    }

    public void setLang(LanguageEnum lang) {
        this.lang = lang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TranslationDTO)) {
            return false;
        }

        TranslationDTO translationDTO = (TranslationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, translationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TranslationDTO{" +
            "id=" + getId() +
            ", entityType='" + getEntityType() + "'" +
            ", entityId=" + getEntityId() +
            ", lang='" + getLang() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
