package pl.kurs.mapper;

import org.mapstruct.Mapper;
import pl.kurs.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default Category categoryFromId(Long id) {
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
