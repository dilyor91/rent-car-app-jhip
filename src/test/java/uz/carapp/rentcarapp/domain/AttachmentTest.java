package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.AttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.BrandTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void brandTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        attachment.setBrand(brandBack);
        assertThat(attachment.getBrand()).isEqualTo(brandBack);
        assertThat(brandBack.getAttachment()).isEqualTo(attachment);

        attachment.brand(null);
        assertThat(attachment.getBrand()).isNull();
        assertThat(brandBack.getAttachment()).isNull();
    }
}
