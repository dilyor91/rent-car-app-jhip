package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.ParamAsserts.*;
import static uz.carapp.rentcarapp.domain.ParamTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParamMapperTest {

    private ParamMapper paramMapper;

    @BeforeEach
    void setUp() {
        paramMapper = new ParamMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParamSample1();
        var actual = paramMapper.toEntity(paramMapper.toDto(expected));
        assertParamAllPropertiesEquals(expected, actual);
    }
}
