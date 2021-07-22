package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdf.sim.entity.ExperimentCategoryEntity;

import java.util.List;

@Repository
public interface CourseCategoryDao extends JpaRepository<ExperimentCategoryEntity, String> {
    List<ExperimentCategoryEntity> findAllByCourseID(String courseID);

    void deleteAllByCourseID(String courseID);
}
