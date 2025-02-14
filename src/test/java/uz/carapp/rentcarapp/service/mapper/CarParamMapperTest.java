package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarParamAsserts.*;
import static uz.carapp.rentcarapp.domain.CarParamTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarParamMapperTest {

    private CarParamMapper carParamMapper;

    @BeforeEach
    void setUp() {
        carParamMapper = new CarParamMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarParamSample1();
        var actual = carParamMapper.toEntity(carParamMapper.toDto(expected));
        assertCarParamAllPropertiesEquals(expected, actual);
    }
}
