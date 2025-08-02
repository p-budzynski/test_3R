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
    void shouldReturnCategory() {
        //given
        Category categoryTest = new Category(1L, "Fantasy");
        when(categoryRepositoryMock.findById(categoryTest.getId())).thenReturn(Optional.of(categoryTest));

        //when
        Category result = categoryService.findById(categoryTest.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(categoryTest.getId());
        assertThat(result.getName()).isEqualTo(categoryTest.getName());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotExists() {
        //given
        Long nonExistentCategoryId = 999L;
        when(categoryRepositoryMock.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> categoryService.findById(nonExistentCategoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category ID: 999 not found");
    }

}