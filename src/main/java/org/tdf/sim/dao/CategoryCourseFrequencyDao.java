package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.CategoryCourseFrequencyEntity;

import java.util.Optional;

public interface CategoryCourseFrequencyDao extends JpaRepository<CategoryCourseFrequencyEntity, String> {

    @Query(value = "select * from category_course_frequency where category_id =? and created_at > CURRENT_DATE", nativeQuery = true)
    Optional<CategoryCourseFrequencyEntity> findCurrentVisitCourse(String categoryID);

}
