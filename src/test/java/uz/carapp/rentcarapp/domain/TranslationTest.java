package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.TranslationTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class TranslationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Translation.class);
        Translation translation1 = getTranslationSample1();
        Translation translation2 = new Translation();
        assertThat(translation1).isNotEqualTo(translation2);

        translation2.setId(translation1.getId());
        assertThat(translation1).isEqualTo(translation2);

        translation2 = getTranslationSample2();
        assertThat(translation1).isNotEqualTo(translation2);
    }
}
