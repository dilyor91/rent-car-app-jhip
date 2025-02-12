package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.ParamTestSamples.*;
import static uz.carapp.rentcarapp.domain.ParamValueTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParamValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParamValue.class);
        ParamValue paramValue1 = getParamValueSample1();
        ParamValue paramValue2 = new ParamValue();
        assertThat(paramValue1).isNotEqualTo(paramValue2);

        paramValue2.setId(paramValue1.getId());
        assertThat(paramValue1).isEqualTo(paramValue2);

        paramValue2 = getParamValueSample2();
        assertThat(paramValue1).isNotEqualTo(paramValue2);
    }

    @Test
    void paramTest() {
        ParamValue paramValue = getParamValueRandomSampleGenerator();
        Param paramBack = getParamRandomSampleGenerator();

        paramValue.setParam(paramBack);
        assertThat(paramValue.getParam()).isEqualTo(paramBack);

        paramValue.param(null);
        assertThat(paramValue.getParam()).isNull();
    }
}
