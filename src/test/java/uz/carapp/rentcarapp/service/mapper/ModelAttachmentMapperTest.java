package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.ModelAttachmentAsserts.*;
import static uz.carapp.rentcarapp.domain.ModelAttachmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelAttachmentMapperTest {

    private ModelAttachmentMapper modelAttachmentMapper;

    @BeforeEach
    void setUp() {
        modelAttachmentMapper = new ModelAttachmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getModelAttachmentSample1();
        var actual = modelAttachmentMapper.toEntity(modelAttachmentMapper.toDto(expected));
        assertModelAttachmentAllPropertiesEquals(expected, actual);
    }
}
