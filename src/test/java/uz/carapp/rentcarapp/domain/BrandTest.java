package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.AttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.BrandTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class BrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = getBrandSample1();
        Brand brand2 = new Brand();
        assertThat(brand1).isNotEqualTo(brand2);

        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);

        brand2 = getBrandSample2();
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    void attachmentTest() {
        Brand brand = getBrandRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        brand.setAttachment(attachmentBack);
        assertThat(brand.getAttachment()).isEqualTo(attachmentBack);

        brand.attachment(null);
        assertThat(brand.getAttachment()).isNull();
    }
}
