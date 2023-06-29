package ru.pricehunt.pleerru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pricehunt.pleerru.model.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
