package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarBodyAsserts.*;
import static uz.carapp.rentcarapp.domain.CarBodyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarBodyMapperTest {

    private CarBodyMapper carBodyMapper;

    @BeforeEach
    void setUp() {
        carBodyMapper = new CarBodyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarBodySample1();
        var actual = carBodyMapper.toEntity(carBodyMapper.toDto(expected));
        assertCarBodyAllPropertiesEquals(expected, actual);
    }
}
