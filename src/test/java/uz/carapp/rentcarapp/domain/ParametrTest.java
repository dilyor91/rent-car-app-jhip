package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.ParametrTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParametrTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parametr.class);
        Parametr parametr1 = getParametrSample1();
        Parametr parametr2 = new Parametr();
        assertThat(parametr1).isNotEqualTo(parametr2);

        parametr2.setId(parametr1.getId());
        assertThat(parametr1).isEqualTo(parametr2);

        parametr2 = getParametrSample2();
        assertThat(parametr1).isNotEqualTo(parametr2);
    }
}
