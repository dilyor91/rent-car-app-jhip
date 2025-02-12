package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class TranslationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TranslationDTO.class);
        TranslationDTO translationDTO1 = new TranslationDTO();
        translationDTO1.setId(1L);
        TranslationDTO translationDTO2 = new TranslationDTO();
        assertThat(translationDTO1).isNotEqualTo(translationDTO2);
        translationDTO2.setId(translationDTO1.getId());
        assertThat(translationDTO1).isEqualTo(translationDTO2);
        translationDTO2.setId(2L);
        assertThat(translationDTO1).isNotEqualTo(translationDTO2);
        translationDTO1.setId(null);
        assertThat(translationDTO1).isNotEqualTo(translationDTO2);
    }
}
