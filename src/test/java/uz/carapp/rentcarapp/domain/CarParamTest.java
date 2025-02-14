package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarParamTestSamples.*;
import static uz.carapp.rentcarapp.domain.CarTestSamples.*;
import static uz.carapp.rentcarapp.domain.ParamTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarParamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarParam.class);
        CarParam carParam1 = getCarParamSample1();
        CarParam carParam2 = new CarParam();
        assertThat(carParam1).isNotEqualTo(carParam2);

        carParam2.setId(carParam1.getId());
        assertThat(carParam1).isEqualTo(carParam2);

        carParam2 = getCarParamSample2();
        assertThat(carParam1).isNotEqualTo(carParam2);
    }

    @Test
    void carTest() {
        CarParam carParam = getCarParamRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        carParam.setCar(carBack);
        assertThat(carParam.getCar()).isEqualTo(carBack);

        carParam.car(null);
        assertThat(carParam.getCar()).isNull();
    }

    @Test
    void paramTest() {
        CarParam carParam = getCarParamRandomSampleGenerator();
        Param paramBack = getParamRandomSampleGenerator();

        carParam.setParam(paramBack);
        assertThat(carParam.getParam()).isEqualTo(paramBack);

        carParam.param(null);
        assertThat(carParam.getParam()).isNull();
    }
}
