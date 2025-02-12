package uz.carapp.rentcarapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class ColorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ColorDTO.class);
        ColorDTO colorDTO1 = new ColorDTO();
        colorDTO1.setId(1L);
        ColorDTO colorDTO2 = new ColorDTO();
        assertThat(colorDTO1).isNotEqualTo(colorDTO2);
        colorDTO2.setId(colorDTO1.getId());
        assertThat(colorDTO1).isEqualTo(colorDTO2);
        colorDTO2.setId(2L);
        assertThat(colorDTO1).isNotEqualTo(colorDTO2);
        colorDTO1.setId(null);
        assertThat(colorDTO1).isNotEqualTo(colorDTO2);
    }
}
