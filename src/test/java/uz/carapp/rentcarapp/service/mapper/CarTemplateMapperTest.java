package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarTemplateAsserts.*;
import static uz.carapp.rentcarapp.domain.CarTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarTemplateMapperTest {

    private CarTemplateMapper carTemplateMapper;

    @BeforeEach
    void setUp() {
        carTemplateMapper = new CarTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarTemplateSample1();
        var actual = carTemplateMapper.toEntity(carTemplateMapper.toDto(expected));
        assertCarTemplateAllPropertiesEquals(expected, actual);
    }
}
