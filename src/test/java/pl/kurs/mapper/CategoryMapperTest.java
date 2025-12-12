package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {
    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void shouldReturnCategoryFromId() {
        //given
        Long categoryId = 1L;

        //when
        Category result = categoryMapper.categoryFromId(categoryId);

        //then
        assertThat(result.getId()).isEqualTo(categoryId);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        //when
        Category result = categoryMapper.categoryFromId(null);

        //then
        assertThat(result).isNull();
    }
}