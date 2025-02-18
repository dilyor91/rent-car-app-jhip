package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarTemplateParamAsserts.*;
import static uz.carapp.rentcarapp.domain.CarTemplateParamTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarTemplateParamMapperTest {

    private CarTemplateParamMapper carTemplateParamMapper;

    @BeforeEach
    void setUp() {
        carTemplateParamMapper = new CarTemplateParamMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarTemplateParamSample1();
        var actual = carTemplateParamMapper.toEntity(carTemplateParamMapper.toDto(expected));
        assertCarTemplateParamAllPropertiesEquals(expected, actual);
    }
}
