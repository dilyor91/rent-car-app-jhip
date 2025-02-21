package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarMileageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarMileageDTO.class);
        CarMileageDTO carMileageDTO1 = new CarMileageDTO();
        carMileageDTO1.setId(1L);
        CarMileageDTO carMileageDTO2 = new CarMileageDTO();
        assertThat(carMileageDTO1).isNotEqualTo(carMileageDTO2);
        carMileageDTO2.setId(carMileageDTO1.getId());
        assertThat(carMileageDTO1).isEqualTo(carMileageDTO2);
        carMileageDTO2.setId(2L);
        assertThat(carMileageDTO1).isNotEqualTo(carMileageDTO2);
        carMileageDTO1.setId(null);
        assertThat(carMileageDTO1).isNotEqualTo(carMileageDTO2);
    }
}
