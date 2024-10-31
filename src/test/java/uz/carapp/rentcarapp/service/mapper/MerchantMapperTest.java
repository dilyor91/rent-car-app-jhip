package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.MerchantAsserts.*;
import static uz.carapp.rentcarapp.domain.MerchantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantMapperTest {

    private MerchantMapper merchantMapper;

    @BeforeEach
    void setUp() {
        merchantMapper = new MerchantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMerchantSample1();
        var actual = merchantMapper.toEntity(merchantMapper.toDto(expected));
        assertMerchantAllPropertiesEquals(expected, actual);
    }
}
