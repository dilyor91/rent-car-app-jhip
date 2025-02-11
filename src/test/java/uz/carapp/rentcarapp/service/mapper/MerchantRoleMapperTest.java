package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.MerchantRoleAsserts.*;
import static uz.carapp.rentcarapp.domain.MerchantRoleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantRoleMapperTest {

    private MerchantRoleMapper merchantRoleMapper;

    @BeforeEach
    void setUp() {
        merchantRoleMapper = new MerchantRoleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMerchantRoleSample1();
        var actual = merchantRoleMapper.toEntity(merchantRoleMapper.toDto(expected));
        assertMerchantRoleAllPropertiesEquals(expected, actual);
    }
}
