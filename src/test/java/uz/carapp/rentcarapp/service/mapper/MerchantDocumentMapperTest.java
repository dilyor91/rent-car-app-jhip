package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.MerchantDocumentAsserts.*;
import static uz.carapp.rentcarapp.domain.MerchantDocumentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantDocumentMapperTest {

    private MerchantDocumentMapper merchantDocumentMapper;

    @BeforeEach
    void setUp() {
        merchantDocumentMapper = new MerchantDocumentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMerchantDocumentSample1();
        var actual = merchantDocumentMapper.toEntity(merchantDocumentMapper.toDto(expected));
        assertMerchantDocumentAllPropertiesEquals(expected, actual);
    }
}
