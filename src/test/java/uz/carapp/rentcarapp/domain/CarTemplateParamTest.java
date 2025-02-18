package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarTemplateParamTestSamples.*;
import static uz.carapp.rentcarapp.domain.CarTemplateTestSamples.*;
import static uz.carapp.rentcarapp.domain.ParamTestSamples.*;
import static uz.carapp.rentcarapp.domain.ParamValueTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarTemplateParamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarTemplateParam.class);
        CarTemplateParam carTemplateParam1 = getCarTemplateParamSample1();
        CarTemplateParam carTemplateParam2 = new CarTemplateParam();
        assertThat(carTemplateParam1).isNotEqualTo(carTemplateParam2);

        carTemplateParam2.setId(carTemplateParam1.getId());
        assertThat(carTemplateParam1).isEqualTo(carTemplateParam2);

        carTemplateParam2 = getCarTemplateParamSample2();
        assertThat(carTemplateParam1).isNotEqualTo(carTemplateParam2);
    }

    @Test
    void carTemplateTest() {
        CarTemplateParam carTemplateParam = getCarTemplateParamRandomSampleGenerator();
        CarTemplate carTemplateBack = getCarTemplateRandomSampleGenerator();

        carTemplateParam.setCarTemplate(carTemplateBack);
        assertThat(carTemplateParam.getCarTemplate()).isEqualTo(carTemplateBack);

        carTemplateParam.carTemplate(null);
        assertThat(carTemplateParam.getCarTemplate()).isNull();
    }

    @Test
    void paramTest() {
        CarTemplateParam carTemplateParam = getCarTemplateParamRandomSampleGenerator();
        Param paramBack = getParamRandomSampleGenerator();

        carTemplateParam.setParam(paramBack);
        assertThat(carTemplateParam.getParam()).isEqualTo(paramBack);

        carTemplateParam.param(null);
        assertThat(carTemplateParam.getParam()).isNull();
    }

    @Test
    void paramValueTest() {
        CarTemplateParam carTemplateParam = getCarTemplateParamRandomSampleGenerator();
        ParamValue paramValueBack = getParamValueRandomSampleGenerator();

        carTemplateParam.setParamValue(paramValueBack);
        assertThat(carTemplateParam.getParamValue()).isEqualTo(paramValueBack);

        carTemplateParam.paramValue(null);
        assertThat(carTemplateParam.getParamValue()).isNull();
    }
}
