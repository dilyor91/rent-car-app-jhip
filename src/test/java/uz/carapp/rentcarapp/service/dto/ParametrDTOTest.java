package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParametrDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParametrDTO.class);
        ParametrDTO parametrDTO1 = new ParametrDTO();
        parametrDTO1.setId(1L);
        ParametrDTO parametrDTO2 = new ParametrDTO();
        assertThat(parametrDTO1).isNotEqualTo(parametrDTO2);
        parametrDTO2.setId(parametrDTO1.getId());
        assertThat(parametrDTO1).isEqualTo(parametrDTO2);
        parametrDTO2.setId(2L);
        assertThat(parametrDTO1).isNotEqualTo(parametrDTO2);
        parametrDTO1.setId(null);
        assertThat(parametrDTO1).isNotEqualTo(parametrDTO2);
    }
}
