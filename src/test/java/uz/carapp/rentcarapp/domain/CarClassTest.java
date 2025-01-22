package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarClassTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarClassTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarClass.class);
        CarClass carClass1 = getCarClassSample1();
        CarClass carClass2 = new CarClass();
        assertThat(carClass1).isNotEqualTo(carClass2);

        carClass2.setId(carClass1.getId());
        assertThat(carClass1).isEqualTo(carClass2);

        carClass2 = getCarClassSample2();
        assertThat(carClass1).isNotEqualTo(carClass2);
    }
}
