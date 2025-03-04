package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.DocAttachmentAsserts.*;
import static uz.carapp.rentcarapp.domain.DocAttachmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocAttachmentMapperTest {

    private DocAttachmentMapper docAttachmentMapper;

    @BeforeEach
    void setUp() {
        docAttachmentMapper = new DocAttachmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDocAttachmentSample1();
        var actual = docAttachmentMapper.toEntity(docAttachmentMapper.toDto(expected));
        assertDocAttachmentAllPropertiesEquals(expected, actual);
    }
}
