package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarClassDTO.class);
        CarClassDTO carClassDTO1 = new CarClassDTO();
        carClassDTO1.setId(1L);
        CarClassDTO carClassDTO2 = new CarClassDTO();
        assertThat(carClassDTO1).isNotEqualTo(carClassDTO2);
        carClassDTO2.setId(carClassDTO1.getId());
        assertThat(carClassDTO1).isEqualTo(carClassDTO2);
        carClassDTO2.setId(2L);
        assertThat(carClassDTO1).isNotEqualTo(carClassDTO2);
        carClassDTO1.setId(null);
        assertThat(carClassDTO1).isNotEqualTo(carClassDTO2);
    }
}
