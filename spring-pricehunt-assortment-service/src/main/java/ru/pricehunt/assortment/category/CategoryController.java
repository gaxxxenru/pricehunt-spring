package ru.pricehunt.assortment.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.CategoryDTO;
import ru.pricehunt.assortment.dto.CategoryTreeDTO;
import ru.pricehunt.assortment.mapper.CategoryMapper;
import ru.pricehunt.assortment.model.Category;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
@Tag(name = "Category Controller", description = "Контроллер для работы с категориями товаров")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Список всех категорий", description = "Возвращает список всех категорий")
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.categoriesToCategoryDTOs(categoryService.findAll());
    }

    @GetMapping("/root")
    @Operation(summary = "Список корневых категорий", description = "Возвращает список корневых категорий")
    public List<CategoryDTO> getRootCategories() {
        return categoryMapper.categoriesToCategoryDTOs(categoryService.findByParentSlugIsNull());
    }

    @GetMapping("/{parentSlug}/children")
    @Operation(summary = "Список дочерних категорий", description = "Возвращает список дочерних категорий")
    public List<CategoryDTO> getChildrenCategories(@PathVariable String parentSlug) {
        return categoryMapper.categoriesToCategoryDTOs(categoryService.findByParentSlug(parentSlug));
    }


    @GetMapping("/{slug}")
    @Operation(summary = "Категория по slug", description = "Возвращает категорию по slug")
    public CategoryTreeDTO getCategoryBySlug(@PathVariable String slug) {
        return categoryMapper.categoryToCategoryTreeDTO(categoryService.findBySlug(slug));
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Обновление категории", description = "Обновляет категорию по slug")
    public CategoryDTO updateCategory(@PathVariable String slug, @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        return categoryMapper.categoryToCategoryDTO(categoryService.update(slug, category));
    }

    @PostMapping("/{parentSlug}/addChild")
    @Operation(summary = "Добавление дочерней категории", description = "Добавляет дочернюю категорию")
    public CategoryDTO addChildCategory(@PathVariable String parentSlug, @RequestBody Category childCategory) {
        return categoryMapper.categoryToCategoryDTO(categoryService.addChild(parentSlug, childCategory));
    }

    @DeleteMapping("/{parentSlug}/removeChild/{childSlug}")
    @Operation(summary = "Удаление дочерней категории", description = "Удаляет дочернюю категорию")
    public CategoryDTO removeChild(@PathVariable String parentSlug, @PathVariable String childSlug) {
        return categoryMapper.categoryToCategoryDTO(categoryService.removeChild(parentSlug, childSlug));
    }

    @PostMapping
    @Operation(summary = "Создание категории", description = "Создает категорию")
    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        return categoryMapper.categoryToCategoryDTO(categoryService.save(category));
    }
}
