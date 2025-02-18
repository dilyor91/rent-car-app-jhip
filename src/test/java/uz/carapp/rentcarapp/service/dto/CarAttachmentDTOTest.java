package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarAttachmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarAttachmentDTO.class);
        CarAttachmentDTO carAttachmentDTO1 = new CarAttachmentDTO();
        carAttachmentDTO1.setId(1L);
        CarAttachmentDTO carAttachmentDTO2 = new CarAttachmentDTO();
        assertThat(carAttachmentDTO1).isNotEqualTo(carAttachmentDTO2);
        carAttachmentDTO2.setId(carAttachmentDTO1.getId());
        assertThat(carAttachmentDTO1).isEqualTo(carAttachmentDTO2);
        carAttachmentDTO2.setId(2L);
        assertThat(carAttachmentDTO1).isNotEqualTo(carAttachmentDTO2);
        carAttachmentDTO1.setId(null);
        assertThat(carAttachmentDTO1).isNotEqualTo(carAttachmentDTO2);
    }
}
