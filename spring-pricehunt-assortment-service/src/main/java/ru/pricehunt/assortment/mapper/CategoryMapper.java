package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.CategoryTreeDTO;
import ru.pricehunt.assortment.model.Category;
import ru.pricehunt.assortment.dto.CategoryDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface CategoryMapper {
    Category categoryDTOToCategory(CategoryDTO categoryDTO);
    CategoryDTO categoryToCategoryDTO(Category category);

    CategoryTreeDTO categoryToCategoryTreeDTO(Category category);

    List<CategoryDTO> categoriesToCategoryDTOs(List<Category> categories);
    List<Category> categoryDTOsToCategories(List<CategoryDTO> categoryDTOs);


}
