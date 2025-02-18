package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.AttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.CarAttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.CarTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarAttachment.class);
        CarAttachment carAttachment1 = getCarAttachmentSample1();
        CarAttachment carAttachment2 = new CarAttachment();
        assertThat(carAttachment1).isNotEqualTo(carAttachment2);

        carAttachment2.setId(carAttachment1.getId());
        assertThat(carAttachment1).isEqualTo(carAttachment2);

        carAttachment2 = getCarAttachmentSample2();
        assertThat(carAttachment1).isNotEqualTo(carAttachment2);
    }

    @Test
    void carTest() {
        CarAttachment carAttachment = getCarAttachmentRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        carAttachment.setCar(carBack);
        assertThat(carAttachment.getCar()).isEqualTo(carBack);

        carAttachment.car(null);
        assertThat(carAttachment.getCar()).isNull();
    }

    @Test
    void attachmentTest() {
        CarAttachment carAttachment = getCarAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        carAttachment.setAttachment(attachmentBack);
        assertThat(carAttachment.getAttachment()).isEqualTo(attachmentBack);

        carAttachment.attachment(null);
        assertThat(carAttachment.getAttachment()).isNull();
    }
}
