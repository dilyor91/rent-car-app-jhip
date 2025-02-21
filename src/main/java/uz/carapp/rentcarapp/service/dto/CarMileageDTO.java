package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import uz.carapp.rentcarapp.domain.enumeration.MileageEnum;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.CarMileage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarMileageDTO implements Serializable {

    private Long id;

    private BigDecimal value;

    private MileageEnum unit;

    private Instant date;

    private CarDTO car;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public MileageEnum getUnit() {
        return unit;
    }

    public void setUnit(MileageEnum unit) {
        this.unit = unit;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarMileageDTO)) {
            return false;
        }

        CarMileageDTO carMileageDTO = (CarMileageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carMileageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarMileageDTO{" +
            "id=" + getId() +
            ", value=" + getValue() +
            ", unit='" + getUnit() + "'" +
            ", date='" + getDate() + "'" +
            ", car=" + getCar() +
            "}";
    }
}
