package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.MerchantBranchAsserts.*;
import static uz.carapp.rentcarapp.domain.MerchantBranchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantBranchMapperTest {

    private MerchantBranchMapper merchantBranchMapper;

    @BeforeEach
    void setUp() {
        merchantBranchMapper = new MerchantBranchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMerchantBranchSample1();
        var actual = merchantBranchMapper.toEntity(merchantBranchMapper.toDto(expected));
        assertMerchantBranchAllPropertiesEquals(expected, actual);
    }
}
