package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.CourseFrequencyInfoEntity;

public interface CourseFrequencyInfoDao extends JpaRepository<CourseFrequencyInfoEntity, String> {

    @Query(value = "select count (id) from course_frequency_info where user_id =? and category_id =? and created_at > CURRENT_DATE", nativeQuery = true)
    int countByVisitCourse(String userID,String categoryID);

}
