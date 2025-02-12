package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.TranslationAsserts.*;
import static uz.carapp.rentcarapp.domain.TranslationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranslationMapperTest {

    private TranslationMapper translationMapper;

    @BeforeEach
    void setUp() {
        translationMapper = new TranslationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTranslationSample1();
        var actual = translationMapper.toEntity(translationMapper.toDto(expected));
        assertTranslationAllPropertiesEquals(expected, actual);
    }
}
