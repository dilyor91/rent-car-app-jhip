package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.MerchantBranchTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantRoleTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantRole.class);
        MerchantRole merchantRole1 = getMerchantRoleSample1();
        MerchantRole merchantRole2 = new MerchantRole();
        assertThat(merchantRole1).isNotEqualTo(merchantRole2);

        merchantRole2.setId(merchantRole1.getId());
        assertThat(merchantRole1).isEqualTo(merchantRole2);

        merchantRole2 = getMerchantRoleSample2();
        assertThat(merchantRole1).isNotEqualTo(merchantRole2);
    }

    @Test
    void merchantTest() {
        MerchantRole merchantRole = getMerchantRoleRandomSampleGenerator();
        Merchant merchantBack = getMerchantRandomSampleGenerator();

        merchantRole.setMerchant(merchantBack);
        assertThat(merchantRole.getMerchant()).isEqualTo(merchantBack);

        merchantRole.merchant(null);
        assertThat(merchantRole.getMerchant()).isNull();
    }

    @Test
    void merchantBranchTest() {
        MerchantRole merchantRole = getMerchantRoleRandomSampleGenerator();
        MerchantBranch merchantBranchBack = getMerchantBranchRandomSampleGenerator();

        merchantRole.setMerchantBranch(merchantBranchBack);
        assertThat(merchantRole.getMerchantBranch()).isEqualTo(merchantBranchBack);

        merchantRole.merchantBranch(null);
        assertThat(merchantRole.getMerchantBranch()).isNull();
    }
}
