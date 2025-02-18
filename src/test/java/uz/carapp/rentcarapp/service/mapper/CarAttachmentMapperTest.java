package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CarAttachmentAsserts.*;
import static uz.carapp.rentcarapp.domain.CarAttachmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarAttachmentMapperTest {

    private CarAttachmentMapper carAttachmentMapper;

    @BeforeEach
    void setUp() {
        carAttachmentMapper = new CarAttachmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarAttachmentSample1();
        var actual = carAttachmentMapper.toEntity(carAttachmentMapper.toDto(expected));
        assertCarAttachmentAllPropertiesEquals(expected, actual);
    }
}
