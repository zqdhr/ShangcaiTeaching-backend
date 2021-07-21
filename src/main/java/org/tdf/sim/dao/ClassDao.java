package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.CategoryEntity;
import org.tdf.sim.entity.ClassEntity;

import java.util.List;
import java.util.Optional;

public interface ClassDao extends JpaRepository<ClassEntity, String> {

    Optional<ClassEntity> findById(String id);

    Optional<ClassEntity> findByClassName(String className);

    Optional<ClassEntity> findByClassNameAndGradeNameAndDepartmentAndMajor(String className,String gradeName,String department,String major);

    @Query(value = "SELECT class.* from class inner JOIN  course_class on class.id = course_class.class_id and course_class.course_id = ?", nativeQuery = true)
    List<ClassEntity> findAllClassesByCourseID(String courseID);

}
