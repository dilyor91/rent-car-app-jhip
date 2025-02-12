package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParamValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParamValueDTO.class);
        ParamValueDTO paramValueDTO1 = new ParamValueDTO();
        paramValueDTO1.setId(1L);
        ParamValueDTO paramValueDTO2 = new ParamValueDTO();
        assertThat(paramValueDTO1).isNotEqualTo(paramValueDTO2);
        paramValueDTO2.setId(paramValueDTO1.getId());
        assertThat(paramValueDTO1).isEqualTo(paramValueDTO2);
        paramValueDTO2.setId(2L);
        assertThat(paramValueDTO1).isNotEqualTo(paramValueDTO2);
        paramValueDTO1.setId(null);
        assertThat(paramValueDTO1).isNotEqualTo(paramValueDTO2);
    }
}
