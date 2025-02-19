package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.AttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.ModelAttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.ModelTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ModelAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModelAttachment.class);
        ModelAttachment modelAttachment1 = getModelAttachmentSample1();
        ModelAttachment modelAttachment2 = new ModelAttachment();
        assertThat(modelAttachment1).isNotEqualTo(modelAttachment2);

        modelAttachment2.setId(modelAttachment1.getId());
        assertThat(modelAttachment1).isEqualTo(modelAttachment2);

        modelAttachment2 = getModelAttachmentSample2();
        assertThat(modelAttachment1).isNotEqualTo(modelAttachment2);
    }

    @Test
    void modelTest() {
        ModelAttachment modelAttachment = getModelAttachmentRandomSampleGenerator();
        Model modelBack = getModelRandomSampleGenerator();

        modelAttachment.setModel(modelBack);
        assertThat(modelAttachment.getModel()).isEqualTo(modelBack);

        modelAttachment.model(null);
        assertThat(modelAttachment.getModel()).isNull();
    }

    @Test
    void attachmentTest() {
        ModelAttachment modelAttachment = getModelAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        modelAttachment.setAttachment(attachmentBack);
        assertThat(modelAttachment.getAttachment()).isEqualTo(attachmentBack);

        modelAttachment.attachment(null);
        assertThat(modelAttachment.getAttachment()).isNull();
    }
}
