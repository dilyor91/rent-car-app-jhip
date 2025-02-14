package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarParamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarParamDTO.class);
        CarParamDTO carParamDTO1 = new CarParamDTO();
        carParamDTO1.setId(1L);
        CarParamDTO carParamDTO2 = new CarParamDTO();
        assertThat(carParamDTO1).isNotEqualTo(carParamDTO2);
        carParamDTO2.setId(carParamDTO1.getId());
        assertThat(carParamDTO1).isEqualTo(carParamDTO2);
        carParamDTO2.setId(2L);
        assertThat(carParamDTO1).isNotEqualTo(carParamDTO2);
        carParamDTO1.setId(null);
        assertThat(carParamDTO1).isNotEqualTo(carParamDTO2);
    }
}
