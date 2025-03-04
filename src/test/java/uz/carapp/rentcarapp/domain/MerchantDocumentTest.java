package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.DocumentTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantDocumentTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class MerchantDocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantDocument.class);
        MerchantDocument merchantDocument1 = getMerchantDocumentSample1();
        MerchantDocument merchantDocument2 = new MerchantDocument();
        assertThat(merchantDocument1).isNotEqualTo(merchantDocument2);

        merchantDocument2.setId(merchantDocument1.getId());
        assertThat(merchantDocument1).isEqualTo(merchantDocument2);

        merchantDocument2 = getMerchantDocumentSample2();
        assertThat(merchantDocument1).isNotEqualTo(merchantDocument2);
    }

    @Test
    void merchantTest() {
        MerchantDocument merchantDocument = getMerchantDocumentRandomSampleGenerator();
        Merchant merchantBack = getMerchantRandomSampleGenerator();

        merchantDocument.setMerchant(merchantBack);
        assertThat(merchantDocument.getMerchant()).isEqualTo(merchantBack);

        merchantDocument.merchant(null);
        assertThat(merchantDocument.getMerchant()).isNull();
    }

    @Test
    void documentTest() {
        MerchantDocument merchantDocument = getMerchantDocumentRandomSampleGenerator();
        Document documentBack = getDocumentRandomSampleGenerator();

        merchantDocument.setDocument(documentBack);
        assertThat(merchantDocument.getDocument()).isEqualTo(documentBack);

        merchantDocument.document(null);
        assertThat(merchantDocument.getDocument()).isNull();
    }
}
