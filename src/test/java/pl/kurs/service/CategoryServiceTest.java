package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.Category;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.CategoryRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnCategoryById() {
        //given
        Long categoryId = 1L;
        Category categoryTest = new Category(categoryId, "Category test");
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(categoryTest));

        //when
        Category result = categoryService.findById(categoryId);

        //then
        assertThat(result).isEqualTo(categoryTest);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        //given
        Long categoryId = 1L;
        when(categoryRepositoryMock.findById(categoryId))
                .thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(STR."Category ID: \{categoryId} not found");
    }

}