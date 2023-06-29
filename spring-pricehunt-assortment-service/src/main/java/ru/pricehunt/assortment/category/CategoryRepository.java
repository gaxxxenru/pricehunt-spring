package ru.pricehunt.assortment.category;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import ru.pricehunt.assortment.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {



    Optional<Category> findBySlug(String slug);

    @NonNull
    @EntityGraph(attributePaths = {"parent", "children"})
    List<Category> findAll();


    @EntityGraph(attributePaths = {"parent", "children"})
    List<Category> findByParentSlug(String parentSlug);

    @EntityGraph(attributePaths = {"parent", "children"})
    List<Category> findByParentSlugIsNull();
}
