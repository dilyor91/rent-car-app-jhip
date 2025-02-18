package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarTemplateTestSamples.*;
import static uz.carapp.rentcarapp.domain.ModelTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarTemplate.class);
        CarTemplate carTemplate1 = getCarTemplateSample1();
        CarTemplate carTemplate2 = new CarTemplate();
        assertThat(carTemplate1).isNotEqualTo(carTemplate2);

        carTemplate2.setId(carTemplate1.getId());
        assertThat(carTemplate1).isEqualTo(carTemplate2);

        carTemplate2 = getCarTemplateSample2();
        assertThat(carTemplate1).isNotEqualTo(carTemplate2);
    }

    @Test
    void modelTest() {
        CarTemplate carTemplate = getCarTemplateRandomSampleGenerator();
        Model modelBack = getModelRandomSampleGenerator();

        carTemplate.setModel(modelBack);
        assertThat(carTemplate.getModel()).isEqualTo(modelBack);

        carTemplate.model(null);
        assertThat(carTemplate.getModel()).isNull();
    }
}
