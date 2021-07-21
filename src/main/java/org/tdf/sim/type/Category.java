package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class Category {

    @JsonProperty("category_id")
    public String categoryID;

    @JsonProperty("category_name_chs")
    public String categoryNameChs;

    @JsonProperty("category_name_en")
    public String categoryNameEn;

    @JsonProperty("parent_id")
    public String parentID;

    public int status;

    public int priority;

    public int type;

    public String annex;
}
