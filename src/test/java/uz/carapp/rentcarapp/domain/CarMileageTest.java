package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarMileageTestSamples.*;
import static uz.carapp.rentcarapp.domain.CarTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarMileageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarMileage.class);
        CarMileage carMileage1 = getCarMileageSample1();
        CarMileage carMileage2 = new CarMileage();
        assertThat(carMileage1).isNotEqualTo(carMileage2);

        carMileage2.setId(carMileage1.getId());
        assertThat(carMileage1).isEqualTo(carMileage2);

        carMileage2 = getCarMileageSample2();
        assertThat(carMileage1).isNotEqualTo(carMileage2);
    }

    @Test
    void carTest() {
        CarMileage carMileage = getCarMileageRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        carMileage.setCar(carBack);
        assertThat(carMileage.getCar()).isEqualTo(carBack);

        carMileage.car(null);
        assertThat(carMileage.getCar()).isNull();
    }
}
