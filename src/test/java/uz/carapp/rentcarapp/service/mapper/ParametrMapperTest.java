package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.ParametrAsserts.*;
import static uz.carapp.rentcarapp.domain.ParametrTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParametrMapperTest {

    private ParametrMapper parametrMapper;

    @BeforeEach
    void setUp() {
        parametrMapper = new ParametrMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParametrSample1();
        var actual = parametrMapper.toEntity(parametrMapper.toDto(expected));
        assertParametrAllPropertiesEquals(expected, actual);
    }
}
