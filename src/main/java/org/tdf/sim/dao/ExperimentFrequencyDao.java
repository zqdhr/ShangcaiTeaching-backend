package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.ExperimentFrequencyEntity;

import java.util.Optional;

public interface ExperimentFrequencyDao extends JpaRepository<ExperimentFrequencyEntity, String> {

    @Query(value = "select * from experiment_frequency where experiment_id =? and created_at > CURRENT_DATE", nativeQuery = true)
    Optional<ExperimentFrequencyEntity> findCurrentVisitExperiment(String experimentId);

}
