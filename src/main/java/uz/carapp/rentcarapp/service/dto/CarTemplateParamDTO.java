package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarTemplateParam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarTemplateParamDTO implements Serializable {

    private Long id;

    private String paramVal;

    private CarTemplateDTO carTemplate;

    private ParamDTO param;

    private ParamValueDTO paramValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

    public CarTemplateDTO getCarTemplate() {
        return carTemplate;
    }

    public void setCarTemplate(CarTemplateDTO carTemplate) {
        this.carTemplate = carTemplate;
    }

    public ParamDTO getParam() {
        return param;
    }

    public void setParam(ParamDTO param) {
        this.param = param;
    }

    public ParamValueDTO getParamValue() {
        return paramValue;
    }

    public void setParamValue(ParamValueDTO paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarTemplateParamDTO)) {
            return false;
        }

        CarTemplateParamDTO carTemplateParamDTO = (CarTemplateParamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carTemplateParamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarTemplateParamDTO{" +
            "id=" + getId() +
            ", paramVal='" + getParamVal() + "'" +
            ", carTemplate=" + getCarTemplate() +
            ", param=" + getParam() +
            ", paramValue=" + getParamValue() +
            "}";
    }
}
