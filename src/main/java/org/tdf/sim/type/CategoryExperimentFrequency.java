package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public class CategoryExperimentFrequency {

    @JsonProperty("experiment_name_chs")
    public String experimentNameChs;

    @JsonProperty("experiment_name_en")
    public String experimentNameEn;

    @JsonProperty("frequency")
    @Getter
    public int frequency;

    @JsonProperty("created_at")
    public LocalDateTime createdAt;
}
