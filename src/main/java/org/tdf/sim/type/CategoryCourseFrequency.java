package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public class CategoryCourseFrequency {

    @JsonProperty("category_name_chs")
    public String categoryNameChs;

    @JsonProperty("category_name_en")
    public String categoryNameEn;

    @JsonProperty("frequency")
    @Getter
    public int frequency;

    @JsonProperty("created_at")
    public LocalDateTime createdAt;
}
