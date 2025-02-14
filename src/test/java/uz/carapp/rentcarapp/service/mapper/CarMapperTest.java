package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarAsserts.*;
import static uz.carapp.rentcarapp.domain.CarTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarMapperTest {

    private CarMapper carMapper;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarSample1();
        var actual = carMapper.toEntity(carMapper.toDto(expected));
        assertCarAllPropertiesEquals(expected, actual);
    }
}
