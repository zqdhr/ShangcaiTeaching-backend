package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdf.sim.entity.CourseExperimentCategoryEntity;

import java.util.List;

@Repository
public interface CourseExperimentCategoryDao extends JpaRepository<CourseExperimentCategoryEntity, String> {
    List<CourseExperimentCategoryEntity> findAllByCourseID(String courseID);

    void deleteAllByCourseID(String courseID);
}
