package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantDocumentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantDocumentDTO.class);
        MerchantDocumentDTO merchantDocumentDTO1 = new MerchantDocumentDTO();
        merchantDocumentDTO1.setId(1L);
        MerchantDocumentDTO merchantDocumentDTO2 = new MerchantDocumentDTO();
        assertThat(merchantDocumentDTO1).isNotEqualTo(merchantDocumentDTO2);
        merchantDocumentDTO2.setId(merchantDocumentDTO1.getId());
        assertThat(merchantDocumentDTO1).isEqualTo(merchantDocumentDTO2);
        merchantDocumentDTO2.setId(2L);
        assertThat(merchantDocumentDTO1).isNotEqualTo(merchantDocumentDTO2);
        merchantDocumentDTO1.setId(null);
        assertThat(merchantDocumentDTO1).isNotEqualTo(merchantDocumentDTO2);
    }
}
