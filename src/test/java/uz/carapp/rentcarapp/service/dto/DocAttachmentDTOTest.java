package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class DocAttachmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocAttachmentDTO.class);
        DocAttachmentDTO docAttachmentDTO1 = new DocAttachmentDTO();
        docAttachmentDTO1.setId(1L);
        DocAttachmentDTO docAttachmentDTO2 = new DocAttachmentDTO();
        assertThat(docAttachmentDTO1).isNotEqualTo(docAttachmentDTO2);
        docAttachmentDTO2.setId(docAttachmentDTO1.getId());
        assertThat(docAttachmentDTO1).isEqualTo(docAttachmentDTO2);
        docAttachmentDTO2.setId(2L);
        assertThat(docAttachmentDTO1).isNotEqualTo(docAttachmentDTO2);
        docAttachmentDTO1.setId(null);
        assertThat(docAttachmentDTO1).isNotEqualTo(docAttachmentDTO2);
    }
}
