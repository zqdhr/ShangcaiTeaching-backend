package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tdf.sim.entity.CourseEntity;

import java.util.List;

@Repository
public interface CourseDao extends JpaRepository<CourseEntity, String> {

    List<CourseEntity> findAllByTypeAndUserID(int type,String ownerID);

    List<CourseEntity> findAllByType(int type);
}
