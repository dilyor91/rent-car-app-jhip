package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.ColorAsserts.*;
import static uz.carapp.rentcarapp.domain.ColorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColorMapperTest {

    private ColorMapper colorMapper;

    @BeforeEach
    void setUp() {
        colorMapper = new ColorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getColorSample1();
        var actual = colorMapper.toEntity(colorMapper.toDto(expected));
        assertColorAllPropertiesEquals(expected, actual);
    }
}
