package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ModelAttachmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModelAttachmentDTO.class);
        ModelAttachmentDTO modelAttachmentDTO1 = new ModelAttachmentDTO();
        modelAttachmentDTO1.setId(1L);
        ModelAttachmentDTO modelAttachmentDTO2 = new ModelAttachmentDTO();
        assertThat(modelAttachmentDTO1).isNotEqualTo(modelAttachmentDTO2);
        modelAttachmentDTO2.setId(modelAttachmentDTO1.getId());
        assertThat(modelAttachmentDTO1).isEqualTo(modelAttachmentDTO2);
        modelAttachmentDTO2.setId(2L);
        assertThat(modelAttachmentDTO1).isNotEqualTo(modelAttachmentDTO2);
        modelAttachmentDTO1.setId(null);
        assertThat(modelAttachmentDTO1).isNotEqualTo(modelAttachmentDTO2);
    }
}
