package ru.pricehunt.assortment.category;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.Category;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findByParentSlug(String parentSlug) {
        return categoryRepository.findByParentSlug(parentSlug);
    }

    public List<Category> findByParentSlugIsNull() {
        return categoryRepository.findByParentSlugIsNull();
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category save(Category category) {
        if (category.getParent() != null) {
            Category parentCategory = categoryRepository.findBySlug(category.getParent().getSlug())
                .orElseThrow(() -> new EntityNotFoundException("Parent Category - " + category.getParent().getSlug() + " не найдена"));
            category.setParent(parentCategory);
        }
        if (category.getChildren() != null) {
            List<Category> children = category.getChildren();
            category.getChildren().clear();
            for (Category child : children) {
                Category existingChild = categoryRepository.findBySlug(child.getSlug())
                    .orElseThrow(() -> new EntityNotFoundException("Child Category - " + child.getSlug() +  " не найдена"));
                category.getChildren().add(existingChild);
            }
        }
        return categoryRepository.save(category);
    }
    @Transactional
    public Category update(String slug, Category category) {
        Category existingCategory = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
        Optional.ofNullable(category.getName()).ifPresent(existingCategory::setName);
        Optional.ofNullable(category.getDescription()).ifPresent(existingCategory::setDescription);
        return categoryRepository.save(existingCategory);
    }
    @Transactional
    public Category addChild(String parentSlug, Category childCategory) {
        Category parentCategory = categoryRepository.findBySlug(parentSlug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
        childCategory.setParent(parentCategory);
        parentCategory.getChildren().add(childCategory);
        return categoryRepository.save(parentCategory);
    }
    @Transactional
    public Category removeChild(String parentSlug, String childSlug) {
        Category parentCategory = categoryRepository.findBySlug(parentSlug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
        Category childCategory = categoryRepository.findBySlug(childSlug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
        parentCategory.getChildren().remove(childCategory);
        return categoryRepository.save(parentCategory);
    }

    @Transactional
    public Category delete(String slug) {
        Category existingCategory = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Category не найдена"));
        categoryRepository.delete(existingCategory);
        return existingCategory;
    }

}
