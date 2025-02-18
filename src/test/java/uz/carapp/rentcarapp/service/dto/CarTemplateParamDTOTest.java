package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarTemplateParamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarTemplateParamDTO.class);
        CarTemplateParamDTO carTemplateParamDTO1 = new CarTemplateParamDTO();
        carTemplateParamDTO1.setId(1L);
        CarTemplateParamDTO carTemplateParamDTO2 = new CarTemplateParamDTO();
        assertThat(carTemplateParamDTO1).isNotEqualTo(carTemplateParamDTO2);
        carTemplateParamDTO2.setId(carTemplateParamDTO1.getId());
        assertThat(carTemplateParamDTO1).isEqualTo(carTemplateParamDTO2);
        carTemplateParamDTO2.setId(2L);
        assertThat(carTemplateParamDTO1).isNotEqualTo(carTemplateParamDTO2);
        carTemplateParamDTO1.setId(null);
        assertThat(carTemplateParamDTO1).isNotEqualTo(carTemplateParamDTO2);
    }
}
