package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.ParamValueAsserts.*;
import static uz.carapp.rentcarapp.domain.ParamValueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParamValueMapperTest {

    private ParamValueMapper paramValueMapper;

    @BeforeEach
    void setUp() {
        paramValueMapper = new ParamValueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParamValueSample1();
        var actual = paramValueMapper.toEntity(paramValueMapper.toDto(expected));
        assertParamValueAllPropertiesEquals(expected, actual);
    }
}
