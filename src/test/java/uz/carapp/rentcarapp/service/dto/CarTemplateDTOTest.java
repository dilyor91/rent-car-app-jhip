package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarTemplateDTO.class);
        CarTemplateDTO carTemplateDTO1 = new CarTemplateDTO();
        carTemplateDTO1.setId(1L);
        CarTemplateDTO carTemplateDTO2 = new CarTemplateDTO();
        assertThat(carTemplateDTO1).isNotEqualTo(carTemplateDTO2);
        carTemplateDTO2.setId(carTemplateDTO1.getId());
        assertThat(carTemplateDTO1).isEqualTo(carTemplateDTO2);
        carTemplateDTO2.setId(2L);
        assertThat(carTemplateDTO1).isNotEqualTo(carTemplateDTO2);
        carTemplateDTO1.setId(null);
        assertThat(carTemplateDTO1).isNotEqualTo(carTemplateDTO2);
    }
}
