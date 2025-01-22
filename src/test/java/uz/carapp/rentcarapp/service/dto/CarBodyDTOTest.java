package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarBodyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarBodyDTO.class);
        CarBodyDTO carBodyDTO1 = new CarBodyDTO();
        carBodyDTO1.setId(1L);
        CarBodyDTO carBodyDTO2 = new CarBodyDTO();
        assertThat(carBodyDTO1).isNotEqualTo(carBodyDTO2);
        carBodyDTO2.setId(carBodyDTO1.getId());
        assertThat(carBodyDTO1).isEqualTo(carBodyDTO2);
        carBodyDTO2.setId(2L);
        assertThat(carBodyDTO1).isNotEqualTo(carBodyDTO2);
        carBodyDTO1.setId(null);
        assertThat(carBodyDTO1).isNotEqualTo(carBodyDTO2);
    }
}
