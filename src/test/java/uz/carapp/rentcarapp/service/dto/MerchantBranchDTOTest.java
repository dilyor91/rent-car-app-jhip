package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantBranchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantBranchDTO.class);
        MerchantBranchDTO merchantBranchDTO1 = new MerchantBranchDTO();
        merchantBranchDTO1.setId(1L);
        MerchantBranchDTO merchantBranchDTO2 = new MerchantBranchDTO();
        assertThat(merchantBranchDTO1).isNotEqualTo(merchantBranchDTO2);
        merchantBranchDTO2.setId(merchantBranchDTO1.getId());
        assertThat(merchantBranchDTO1).isEqualTo(merchantBranchDTO2);
        merchantBranchDTO2.setId(2L);
        assertThat(merchantBranchDTO1).isNotEqualTo(merchantBranchDTO2);
        merchantBranchDTO1.setId(null);
        assertThat(merchantBranchDTO1).isNotEqualTo(merchantBranchDTO2);
    }
}
