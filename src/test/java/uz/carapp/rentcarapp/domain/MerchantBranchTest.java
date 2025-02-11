package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.MerchantBranchTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantBranchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantBranch.class);
        MerchantBranch merchantBranch1 = getMerchantBranchSample1();
        MerchantBranch merchantBranch2 = new MerchantBranch();
        assertThat(merchantBranch1).isNotEqualTo(merchantBranch2);

        merchantBranch2.setId(merchantBranch1.getId());
        assertThat(merchantBranch1).isEqualTo(merchantBranch2);

        merchantBranch2 = getMerchantBranchSample2();
        assertThat(merchantBranch1).isNotEqualTo(merchantBranch2);
    }

    @Test
    void merchantTest() {
        MerchantBranch merchantBranch = getMerchantBranchRandomSampleGenerator();
        Merchant merchantBack = getMerchantRandomSampleGenerator();

        merchantBranch.setMerchant(merchantBack);
        assertThat(merchantBranch.getMerchant()).isEqualTo(merchantBack);

        merchantBranch.merchant(null);
        assertThat(merchantBranch.getMerchant()).isNull();
    }
}
