package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarMileageAsserts.*;
import static uz.carapp.rentcarapp.domain.CarMileageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarMileageMapperTest {

    private CarMileageMapper carMileageMapper;

    @BeforeEach
    void setUp() {
        carMileageMapper = new CarMileageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarMileageSample1();
        var actual = carMileageMapper.toEntity(carMileageMapper.toDto(expected));
        assertCarMileageAllPropertiesEquals(expected, actual);
    }
}
