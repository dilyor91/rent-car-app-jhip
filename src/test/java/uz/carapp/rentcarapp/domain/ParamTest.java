package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.ParamTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Param.class);
        Param param1 = getParamSample1();
        Param param2 = new Param();
        assertThat(param1).isNotEqualTo(param2);

        param2.setId(param1.getId());
        assertThat(param1).isEqualTo(param2);

        param2 = getParamSample2();
        assertThat(param1).isNotEqualTo(param2);
    }
}
