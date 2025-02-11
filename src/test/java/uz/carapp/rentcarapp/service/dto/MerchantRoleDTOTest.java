package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantRoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantRoleDTO.class);
        MerchantRoleDTO merchantRoleDTO1 = new MerchantRoleDTO();
        merchantRoleDTO1.setId(1L);
        MerchantRoleDTO merchantRoleDTO2 = new MerchantRoleDTO();
        assertThat(merchantRoleDTO1).isNotEqualTo(merchantRoleDTO2);
        merchantRoleDTO2.setId(merchantRoleDTO1.getId());
        assertThat(merchantRoleDTO1).isEqualTo(merchantRoleDTO2);
        merchantRoleDTO2.setId(2L);
        assertThat(merchantRoleDTO1).isNotEqualTo(merchantRoleDTO2);
        merchantRoleDTO1.setId(null);
        assertThat(merchantRoleDTO1).isNotEqualTo(merchantRoleDTO2);
    }
}
