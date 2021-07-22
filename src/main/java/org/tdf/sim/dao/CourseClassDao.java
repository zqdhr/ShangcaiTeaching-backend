package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdf.sim.entity.CourseClassEntity;

import java.util.List;

@Repository
public interface CourseClassDao extends JpaRepository<CourseClassEntity, String> {

    List<CourseClassEntity> findAllByCourseID(String courseID);

    List<CourseClassEntity> findAllByClassID(String ClassID);

    void deleteAllByCourseID(String courseID);
}
