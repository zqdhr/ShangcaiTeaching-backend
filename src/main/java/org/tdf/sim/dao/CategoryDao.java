package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tdf.sim.entity.CategoryEntity;

import java.util.List;

@Repository
public interface CategoryDao extends JpaRepository<CategoryEntity, String> {
    @Query(value = "SELECT category.* from category inner JOIN  course_experiment_category on category.id = course_experiment_category.category_id and course_experiment_category.link_id = ?", nativeQuery = true)
    List<CategoryEntity> findAllCategoriesByLinked(String linked);
}
