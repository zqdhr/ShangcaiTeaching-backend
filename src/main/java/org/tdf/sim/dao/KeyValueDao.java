package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdf.sim.entity.KeyValueEntity;
import org.tdf.sim.entity.KeyValueEntityId;

public interface KeyValueDao extends JpaRepository<KeyValueEntity, KeyValueEntityId> {
}
