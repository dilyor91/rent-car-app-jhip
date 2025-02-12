package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ParamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParamDTO.class);
        ParamDTO paramDTO1 = new ParamDTO();
        paramDTO1.setId(1L);
        ParamDTO paramDTO2 = new ParamDTO();
        assertThat(paramDTO1).isNotEqualTo(paramDTO2);
        paramDTO2.setId(paramDTO1.getId());
        assertThat(paramDTO1).isEqualTo(paramDTO2);
        paramDTO2.setId(2L);
        assertThat(paramDTO1).isNotEqualTo(paramDTO2);
        paramDTO1.setId(null);
        assertThat(paramDTO1).isNotEqualTo(paramDTO2);
    }
}
