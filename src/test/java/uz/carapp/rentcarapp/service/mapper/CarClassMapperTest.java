package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarClassAsserts.*;
import static uz.carapp.rentcarapp.domain.CarClassTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarClassMapperTest {

    private CarClassMapper carClassMapper;

    @BeforeEach
    void setUp() {
        carClassMapper = new CarClassMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarClassSample1();
        var actual = carClassMapper.toEntity(carClassMapper.toDto(expected));
        assertCarClassAllPropertiesEquals(expected, actual);
    }
}
