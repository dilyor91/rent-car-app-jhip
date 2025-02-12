package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.BrandTestSamples.*;
import static uz.carapp.rentcarapp.domain.ModelTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Model.class);
        Model model1 = getModelSample1();
        Model model2 = new Model();
        assertThat(model1).isNotEqualTo(model2);

        model2.setId(model1.getId());
        assertThat(model1).isEqualTo(model2);

        model2 = getModelSample2();
        assertThat(model1).isNotEqualTo(model2);
    }

    @Test
    void brandTest() {
        Model model = getModelRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        model.setBrand(brandBack);
        assertThat(model.getBrand()).isEqualTo(brandBack);

        model.brand(null);
        assertThat(model.getBrand()).isNull();
    }
}
