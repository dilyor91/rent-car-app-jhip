package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarBodyTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarBodyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarBody.class);
        CarBody carBody1 = getCarBodySample1();
        CarBody carBody2 = new CarBody();
        assertThat(carBody1).isNotEqualTo(carBody2);

        carBody2.setId(carBody1.getId());
        assertThat(carBody1).isEqualTo(carBody2);

        carBody2 = getCarBodySample2();
        assertThat(carBody1).isNotEqualTo(carBody2);
    }
}
