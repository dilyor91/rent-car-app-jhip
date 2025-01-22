package uz.carapp.rentcarapp.service.mapper;

import static uz.carapp.rentcarapp.domain.CategoryAsserts.*;
import static uz.carapp.rentcarapp.domain.CategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCategorySample1();
        var actual = categoryMapper.toEntity(categoryMapper.toDto(expected));
        assertCategoryAllPropertiesEquals(expected, actual);
    }
}
