package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findById(String id);

    Optional<UserEntity> findByIdAndRoleID(String id,UserEntity.ROLE_ID roleID);

    List<UserEntity> findAllByRoleID(UserEntity.ROLE_ID roleID);

    List<UserEntity> findAllByClassID(String classID);

}
