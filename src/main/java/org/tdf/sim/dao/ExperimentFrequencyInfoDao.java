package org.tdf.sim.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tdf.sim.entity.ExperimentFrequencyInfoEntity;

public interface ExperimentFrequencyInfoDao extends JpaRepository<ExperimentFrequencyInfoEntity, String> {

    @Query(value = "select count (id) from experiment_frequency_info where user_id =? and experiment_id =? and created_at > CURRENT_DATE", nativeQuery = true)
    int countByVisitExperiment(String userID, String experimentID);

}
