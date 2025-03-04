package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.AttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.DocAttachmentTestSamples.*;
import static uz.carapp.rentcarapp.domain.DocumentTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class DocAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocAttachment.class);
        DocAttachment docAttachment1 = getDocAttachmentSample1();
        DocAttachment docAttachment2 = new DocAttachment();
        assertThat(docAttachment1).isNotEqualTo(docAttachment2);

        docAttachment2.setId(docAttachment1.getId());
        assertThat(docAttachment1).isEqualTo(docAttachment2);

        docAttachment2 = getDocAttachmentSample2();
        assertThat(docAttachment1).isNotEqualTo(docAttachment2);
    }

    @Test
    void documentTest() {
        DocAttachment docAttachment = getDocAttachmentRandomSampleGenerator();
        Document documentBack = getDocumentRandomSampleGenerator();

        docAttachment.setDocument(documentBack);
        assertThat(docAttachment.getDocument()).isEqualTo(documentBack);

        docAttachment.document(null);
        assertThat(docAttachment.getDocument()).isNull();
    }

    @Test
    void attachmentTest() {
        DocAttachment docAttachment = getDocAttachmentRandomSampleGenerator();
        Attachment attachmentBack = getAttachmentRandomSampleGenerator();

        docAttachment.setAttachment(attachmentBack);
        assertThat(docAttachment.getAttachment()).isEqualTo(attachmentBack);

        docAttachment.attachment(null);
        assertThat(docAttachment.getAttachment()).isNull();
    }
}
