package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarParam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarParamDTO implements Serializable {

    private Long id;

    private String paramItemValue;

    private String paramValue;

    private CarDTO car;

    private ParamDTO param;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamItemValue() {
        return paramItemValue;
    }

    public void setParamItemValue(String paramItemValue) {
        this.paramItemValue = paramItemValue;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
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
        if (!(o instanceof CarParamDTO)) {
            return false;
        }

        CarParamDTO carParamDTO = (CarParamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carParamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarParamDTO{" +
            "id=" + getId() +
            ", paramItemValue='" + getParamItemValue() + "'" +
            ", paramValue='" + getParamValue() + "'" +
            ", car=" + getCar() +
            ", param=" + getParam() +
            "}";
    }
}
